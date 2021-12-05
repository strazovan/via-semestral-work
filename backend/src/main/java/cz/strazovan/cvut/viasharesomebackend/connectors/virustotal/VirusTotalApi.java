package cz.strazovan.cvut.viasharesomebackend.connectors.virustotal;

import cz.strazovan.cvut.viasharesomebackend.connectors.virustotal.model.CheckResult;

public interface VirusTotalApi {
    CheckResult checkFile(byte[] bytes) throws VirusCheckException;
}
