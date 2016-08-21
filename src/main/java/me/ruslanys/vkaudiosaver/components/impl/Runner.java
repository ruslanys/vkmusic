package me.ruslanys.vkaudiosaver.components.impl;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import me.ruslanys.vkaudiosaver.components.VkClient;
import me.ruslanys.vkaudiosaver.domain.Audio;
import me.ruslanys.vkaudiosaver.exceptions.ApiParamsMissingException;
import me.ruslanys.vkaudiosaver.exceptions.DestinationMissingException;
import me.ruslanys.vkaudiosaver.exceptions.ScraperParamsMissingException;
import me.ruslanys.vkaudiosaver.properties.DownloaderProperties;
import me.ruslanys.vkaudiosaver.properties.VkProperties;
import me.ruslanys.vkaudiosaver.services.DownloadService;
import org.apache.commons.cli.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
@Slf4j

@Component
public class Runner implements CommandLineRunner {

    private final DownloadService downloadService;
    private final DownloaderProperties downloaderProperties;
    private final VkProperties vkProperties;

    private final ApplicationContext context;

    @Qualifier
    private VkClient vkClient;

    @Autowired
    public Runner(ApplicationContext context, DownloadService downloadService, DownloaderProperties downloaderProperties, VkProperties vkProperties) {
        this.context = context;
        this.downloadService = downloadService;
        this.downloaderProperties = downloaderProperties;
        this.vkProperties = vkProperties;
    }

    @Override
    public void run(String... args) throws Exception {
        Settings settings = processArgs(args);
        if (settings == null) return;

        // --
        downloaderProperties.setDestination(settings.getDestination());
        downloaderProperties.setPoolSize(settings.getPoolSize());
        vkProperties.setUsername(settings.getUsername());
        vkProperties.setPassword(settings.getPassword());
        vkProperties.setAccessKey(settings.getAccessKey());

        log.info("MODE: {}", settings.getType());
        switch (settings.getType()) {
            case API:
                vkClient = context.getBean("apiVkClient", VkClient.class);
                break;
            case SCRAPER:
                vkClient = context.getBean("scraperVkClient", VkClient.class);
                break;
        }

        downloadService.init(downloaderProperties);
        vkClient.init(vkProperties);

        // --
        List<Audio> audios = vkClient.getAudio().getItems();
        log.info("Fetched {} audios", audios.size());

        downloadService.download(audios);

        System.out.println();
        System.out.println("----------------------------------------------------------");
        System.out.println("Good bye.");
    }

    private Settings processArgs(String... args) {
        Options options = getOptions();

        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);
            Settings settings = new Settings();

            // destination folder
            if (cmd.getArgList().isEmpty()) {
                throw new DestinationMissingException();
            }
            settings.setDestination(cmd.getArgList().get(0));

            // pool-size
            if (cmd.hasOption("pool-size")) {
                settings.setPoolSize(Integer.valueOf(cmd.getOptionValue("pool-size")));
            }

            // type
            if (cmd.hasOption('t')) {
                Type type = Type.valueOf(cmd.getOptionValue('t').toUpperCase());
                settings.setType(type);
            }

            // scrapper
            if (settings.getType() == Type.SCRAPER) {
                if (!cmd.hasOption('u') || !cmd.hasOption('p')) {
                    throw new ScraperParamsMissingException();
                }

                settings.setUsername(cmd.getOptionValue('u'));
                settings.setPassword(cmd.getOptionValue('p'));
            }

            // api
            if (settings.getType() == Type.API) {
                if (!cmd.hasOption('k')) {
                    throw new ApiParamsMissingException();
                }

                settings.setAccessKey(cmd.getOptionValue('k'));
            }

            return settings;
        } catch (Exception e) {
            System.out.println(e.getMessage());

            System.out.println();
            System.out.println("----------------------------------------------------------");
            HelpFormatter helpFormatter = new HelpFormatter();
            helpFormatter.printHelp("java -jar vkaudiosaver.jar [OPTIONS] [DESTINATION]", options);
            return null;
        }
    }

    private Options getOptions() {
        Options options = new Options();

        // type options
        options.addOption(
                Option.builder("t")
                        .longOpt("type")
                        .hasArg()
                        .argName("API/SCRAPER")
                        .desc("Which method of fetching information should be used (API by default)")
                        .build()
        );

        // api options
        options.addOption(
                Option.builder("k")
                        .longOpt("access-key")
                        .desc("VK API access token")
                        .hasArg()
                        .argName("token")
                        .build()
        );

        // scraper options
        options.addOption(
                Option.builder("u")
                        .longOpt("username")
                        .desc("VK username")
                        .hasArg()
                        .argName("email/phone number")
                        .build()
        );
        options.addOption(
                Option.builder("p")
                        .longOpt("password")
                        .desc("VK password")
                        .hasArg()
                        .argName("password")
                        .build()
        );

        // downloader
        options.addOption(
                Option.builder()
                        .longOpt("pool-size")
                        .desc("Download pool size (5 by default)")
                        .hasArg()
                        .argName("number")
                        .type(Integer.class)
                        .build()
        );
        return options;
    }

    @Data
    private static class Settings {

        private String username;
        private String password;
        private String accessKey;

        private String destination;

        private Integer poolSize = 5;
        private Type type = Type.API;
    }

    private enum Type {
        API, SCRAPER
    }

}
