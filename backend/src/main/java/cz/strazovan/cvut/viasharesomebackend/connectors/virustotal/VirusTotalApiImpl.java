package cz.strazovan.cvut.viasharesomebackend.connectors.virustotal;

import cz.strazovan.cvut.viasharesomebackend.connectors.virustotal.dto.AnalysisResponse;
import cz.strazovan.cvut.viasharesomebackend.connectors.virustotal.dto.UploadResponse;
import cz.strazovan.cvut.viasharesomebackend.connectors.virustotal.model.CheckResult;
import cz.strazovan.cvut.viasharesomebackend.utils.NamedByteArrayResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

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
            final String analysisId = this.uploadTheFile(bytes);
            int attempts = 1;
            AnalysisResponse analysisResponse = this.downloadAnalysis(analysisId);
            // analysis can be in state "queued", so we may have to wait
            while (!"completed".equals(analysisResponse.getData().getAttributes().getStatus()) && attempts < this.analysisAttemptsLimit) {
                TimeUnit.MILLISECONDS.sleep(this.analysisAttemptsTimeout);
                analysisResponse = this.downloadAnalysis(analysisId);
            }
            if (!"completed".equals(analysisResponse.getData().getAttributes().getStatus())) {
                throw new VirusCheckException("Failed to check the file."); // todo consider returning unknown instead of failing
            }
            final AnalysisResponse.Data.Attributes.Stats stats = analysisResponse.getData().getAttributes().getStats();
            if (stats.getMalicious() > 0 || stats.getSuspicious() > 0) {
                return CheckResult.FAILED;
            }
            return CheckResult.OK;
        } catch (RestClientException | InterruptedException e) {
            throw new VirusCheckException(e);
        }
    }

    private String uploadTheFile(byte[] bytes) {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("x-apikey", this.token);
        final MultiValueMap<String, Object> values = new LinkedMultiValueMap<>();
        values.add("file", new NamedByteArrayResource("file", bytes));
        final HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(values, headers);
        final RestTemplate restTemplate = new RestTemplate();
        final ResponseEntity<UploadResponse> response = restTemplate.exchange(this.baseURL + "/files", HttpMethod.POST, request, UploadResponse.class);
        return response.getBody().getData().getId();
    }

    private AnalysisResponse downloadAnalysis(String id) {
        final HttpHeaders headers = new HttpHeaders();
        headers.set("x-apikey", this.token);
        final HttpEntity<?> request = new HttpEntity<>(headers.toSingleValueMap());
        final RestTemplate restTemplate = new RestTemplate();
        final ResponseEntity<AnalysisResponse> response = restTemplate.exchange(this.baseURL + "/analyses/" + id, HttpMethod.GET, request, AnalysisResponse.class);
        return response.getBody();
    }

}
