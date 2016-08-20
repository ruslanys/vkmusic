package me.ruslanys.vkaudiosaver.components.impl;

import me.ruslanys.vkaudiosaver.domain.Audio;
import me.ruslanys.vkaudiosaver.properties.DownloaderProperties;
import me.ruslanys.vkaudiosaver.services.DownloadService;
import org.apache.commons.cli.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
@Component
public class Runner implements CommandLineRunner {

    private final ScraperVkClient vkClient;
    private final DownloadService downloadService;
    private final DownloaderProperties downloaderProperties;

    @Autowired
    public Runner(DownloaderProperties downloaderProperties, ScraperVkClient vkClient, DownloadService downloadService) {
        this.downloaderProperties = downloaderProperties;
        this.vkClient = vkClient;
        this.downloadService = downloadService;
    }

    @Override
    public void run(String... args) throws Exception {
        Options options = getOptions();

        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);
            if (cmd.getArgList().isEmpty()) {
                throw new IllegalStateException("Destination folder is missing");
            }

            downloaderProperties.setDestination(cmd.getArgList().get(0));

            if (cmd.hasOption("pool-size")) {
                downloaderProperties.setPoolSize(Integer.valueOf(cmd.getOptionValue("pool-size")));
            }

            // --
            downloadService.init(downloaderProperties);
            vkClient.login(cmd.getOptionValue("username"), cmd.getOptionValue("password"));

            List<Audio> audios = vkClient.getAudio().getItems();
            downloadService.download(audios);
        } catch (Exception e) {
            System.out.println(e.getMessage());

            System.out.println();
            System.out.println("----------------------------------------------------------");
            HelpFormatter helpFormatter = new HelpFormatter();
            helpFormatter.printHelp("java -jar vkaudiosaver.jar [OPTIONS] [DESTINATION]", options);
        }

    }

    private Options getOptions() {
        Options options = new Options();
        options.addOption(
                Option.builder("u")
                        .longOpt("username")
                        .desc("VK username")
                        .required()
                        .hasArg()
                        .argName("email/phone number")
                        .build()
        );
        options.addOption(
                Option.builder("p")
                        .longOpt("password")
                        .desc("VK password")
                        .required()
                        .hasArg()
                        .argName("password")
                        .build()
        );
        options.addOption(
                Option.builder()
                        .longOpt("pool-size")
                        .desc("Download service pool size (5 by default)")
                        .hasArg()
                        .argName("number")
                        .type(Integer.class)
                        .build()
        );
        return options;
    }

}
