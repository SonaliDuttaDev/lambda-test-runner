package uk.co.automatictester.lambdatestrunner.process;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import static java.lang.ProcessBuilder.Redirect;
import static java.nio.charset.StandardCharsets.UTF_8;

public class ProcessRunner {

    private static final Logger log = LogManager.getLogger(ProcessRunner.class);

    private ProcessRunner() {
    }

    public static ProcessResult runProcess(List<String> command, File workDir, Map<String, String> extraEnvVars) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command)
                    .directory(workDir)
                    .redirectOutput(Redirect.PIPE)
                    .redirectErrorStream(true);

            Map<String, String> envVars = processBuilder.environment();
            envVars.putAll(extraEnvVars);

            Process process = processBuilder.start();
            BufferedReader bReader = new BufferedReader(new InputStreamReader(process.getInputStream(), UTF_8));

            StringBuilder processOutput = new StringBuilder();
            String line = "";
            while ((line = bReader.readLine()) != null) {
                log.debug(line);
                processOutput.append(line).append("\n");
            }

            ProcessResult result = new ProcessResult();
            result.setExitCode(process.waitFor());
            result.setOutput(processOutput.toString());
            return result;

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}