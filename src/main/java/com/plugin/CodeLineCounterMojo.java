package com.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.*;
import java.nio.file.*;
import java.util.stream.Stream;


@Mojo(name = "count-lines")
public class CodeLineCounterMojo extends AbstractMojo {

    @Parameter(property = "sourceDirectory", defaultValue = "${project.basedir}/src/main/java")
    private File sourceDirectory;

    public void execute() throws MojoExecutionException, MojoFailureException {
        if (!sourceDirectory.exists()) {
            getLog().warn("Source directory not found: " + sourceDirectory);
            return;
        }

        try {
            long totalLines = countLinesInDirectory(sourceDirectory.toPath());
            getLog().info("=====================================");
            getLog().info("Total Lines of Code: " + totalLines);
            getLog().info("=====================================");
        } catch (IOException e) {
            throw new MojoExecutionException("Error counting lines", e);
        }
    }

    private long countLinesInDirectory(Path directory) throws IOException {
        try (Stream<Path> paths = Files.walk(directory)) {
            return paths.filter(path -> path.toString().endsWith(".java"))
                    .mapToLong(this::countLinesInFile)
                    .sum();
        }
    }

    private long countLinesInFile(Path filePath) {
        try (Stream<String> lines = Files.lines(filePath)) {
            return lines.count();
        } catch (IOException e) {
            getLog().error("Error reading file: " + filePath, e);
            return 0;
        }
    }
}
