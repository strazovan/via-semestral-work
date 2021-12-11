package cz.strazovan.cvut.viasharesomebackend.connectors.virustotal;

import cz.strazovan.cvut.viasharesomebackend.connectors.virustotal.dto.AnalysisResponse;
import cz.strazovan.cvut.viasharesomebackend.connectors.virustotal.dto.GetFileResponse;
import cz.strazovan.cvut.viasharesomebackend.connectors.virustotal.dto.UploadResponse;
import cz.strazovan.cvut.viasharesomebackend.connectors.virustotal.model.CheckResult;
import cz.strazovan.cvut.viasharesomebackend.utils.NamedByteArrayResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
public class VirusTotalApiImpl implements VirusTotalApi {
    @Value("${virusCheck.baseURL}")
    private String baseURL;
    @Value("${virusCheck.token}")
    private String token;
    @Value("${virusCheck.analysisAttempts.limit}")
    private int analysisAttemptsLimit;
    @Value("${virusCheck.analysisAttempts.timeoutMs}")
    private long analysisAttemptsTimeout;

    @Override
    public CheckResult checkFile(byte[] bytes) throws VirusCheckException {
        try {
            // first we try to check if the file wasn't already analysed
            final GetFileResponse fileReport = this.getFileReport(bytes);
            if (fileReport.getError() != null && fileReport.getError().isResourceNotFound()) {
                // if the file hasn't been analysed yet, we upload it and check its analysis
                final String analysisId = this.uploadTheFile(bytes);
                int attempts = 1;
                AnalysisResponse analysisResponse = this.downloadAnalysis(analysisId);
                // analysis can be in state "queued", so we may have to wait
                while (!"completed".equals(analysisResponse.getData().getAttributes().getStatus()) && attempts < this.analysisAttemptsLimit) {
                    TimeUnit.MILLISECONDS.sleep(this.analysisAttemptsTimeout);
                    analysisResponse = this.downloadAnalysis(analysisId);
                    attempts++;
                }
                if (!"completed".equals(analysisResponse.getData().getAttributes().getStatus())) {
                    throw new VirusCheckException("Failed to check the file."); // todo consider returning unknown instead of failing
                }
                final AnalysisResponse.Data.Attributes.Stats stats = analysisResponse.getData().getAttributes().getStats();
                if (stats.getMalicious() > 0 || stats.getSuspicious() > 0) {
                    return CheckResult.FAILED;
                }
                return CheckResult.OK;
            } else {
                return fileReport.getData().getAttributes().getTotalVotes().getMalicious() == 0 ? CheckResult.OK : CheckResult.FAILED;
            }
        } catch (RestClientException | InterruptedException e) {
            throw new VirusCheckException(e);
        }
    }

    private GetFileResponse getFileReport(byte[] file) {
        final HttpHeaders headers = new HttpHeaders();
        headers.set("X-Apikey", this.token);
        final HttpEntity<?> request = new HttpEntity<>(null, headers);
        final RestTemplate restTemplate = new RestTemplate();
        // api returns 404 on not found, however we want the response anyway
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                // nop
            }
        });
        final ResponseEntity<GetFileResponse> response = restTemplate.exchange(this.baseURL + "/files/" + getFileHash(file), HttpMethod.GET, request, GetFileResponse.class);
        return response.getBody();
    }

    private String uploadTheFile(byte[] bytes) {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("X-Apikey", this.token);
        final MultiValueMap<String, Object> values = new LinkedMultiValueMap<>();
        values.add("file", new NamedByteArrayResource("file", bytes));
        final HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(values, headers);
        final RestTemplate restTemplate = new RestTemplate();
        final ResponseEntity<UploadResponse> response = restTemplate.exchange(this.baseURL + "/files", HttpMethod.POST, request, UploadResponse.class);
        return response.getBody().getData().getId();
    }

    private AnalysisResponse downloadAnalysis(String id) {
        final HttpHeaders headers = new HttpHeaders();
        headers.set("X-Apikey", this.token);
        final HttpEntity<?> request = new HttpEntity<>(null, headers);
        final RestTemplate restTemplate = new RestTemplate();
        final ResponseEntity<AnalysisResponse> response = restTemplate.exchange(this.baseURL + "/analyses/" + id, HttpMethod.GET, request, AnalysisResponse.class);
        return response.getBody();
    }

    private String getFileHash(byte[] bytes) {
        return DigestUtils.md5DigestAsHex(bytes);
    }
}
