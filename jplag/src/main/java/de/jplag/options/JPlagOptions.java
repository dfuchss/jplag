package de.jplag.options;

import static de.jplag.options.Verbosity.LONG;
import static de.jplag.strategy.ComparisonMode.NORMAL;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.JPlag;
import de.jplag.Language;
import de.jplag.clustering.ClusteringOptions;
import de.jplag.strategy.ComparisonMode;

/**
 * This record defines the options to configure {@link JPlag}.
 * @param languageOption Language to use when parsing the submissions.
 * @param comparisonMode Determines which strategy to use for the comparison of submissions.
 * @param debugParser If true, submissions that cannot be parsed will be stored in a separate directory.
 * @param fileSuffixes List of file suffixes that should be included.
 * @param similarityThreshold Percentage value (must be between 0 and 100). Comparisons (of submissions pairs) with a
 * similarity below this threshold will be ignored. The default value of 0 allows all matches to be stored. This affects
 * which comparisons are stored and thus make it into the result object. See also {@link #similarityMetric()}.
 * @param maximumNumberOfComparisons The maximum number of comparisons that will be shown in the generated report. If
 * set to -1 all comparisons will be shown.
 * @param similarityMetric The similarity metric determines how the minimum similarity threshold required for a
 * comparison (of two submissions) is calculated. This affects which comparisons are stored and thus make it into the
 * result object.
 * @param minimumTokenMatch Tunes the comparison sensitivity by adjusting the minimum token required to be counted as
 * matching section. A smaller {@code <n>} increases the sensitivity but might lead to more false-positives.
 * @param exclusionFileName Name of the file that contains the names of files to exclude from comparison.
 * @param submissionDirectories Directories with new submissions. These must be checked for plagiarism.
 * @param oldSubmissionDirectories Directories with old submissions to check against.
 * @param baseCodeSubmissionName Path name of the directory containing the base code.
 * @param subdirectoryName Example: If the subdirectoryName is 'src', only the code inside submissionDir/src of each
 * submission will be used for comparison.
 * @param verbosity Level of output verbosity.
 * @param clusteringOptions Clustering options
 */
public record JPlagOptions(LanguageOption languageOption, ComparisonMode comparisonMode, boolean debugParser, List<String> fileSuffixes,
        float similarityThreshold, Integer maximumNumberOfComparisons, SimilarityMetric similarityMetric, Integer minimumTokenMatch,
        String exclusionFileName, List<String> submissionDirectories, List<String> oldSubmissionDirectories, String baseCodeSubmissionName,
        String subdirectoryName, Verbosity verbosity, ClusteringOptions clusteringOptions) {

    public static final ComparisonMode DEFAULT_COMPARISON_MODE = NORMAL;
    public static final float DEFAULT_SIMILARITY_THRESHOLD = 0;
    public static final int DEFAULT_SHOWN_COMPARISONS = 30;
    public static final SimilarityMetric DEFAULT_SIMILARITY_METRIC = SimilarityMetric.AVG;
    public static final Charset CHARSET = StandardCharsets.UTF_8;

    private static final Logger logger = LoggerFactory.getLogger(JPlag.class);

    public JPlagOptions(LanguageOption languageOption, boolean debugParser, List<String> fileSuffixes, Float similarityThreshold,
            int maximumNumberOfComparisons, Integer minimumTokenMatch, String exclusionFileName, List<String> submissionDirectories,
            List<String> oldSubmissionDirectories, String baseCodeSubmissionName, String subdirectoryName, Verbosity verbosity) {
        this(languageOption, //
                DEFAULT_COMPARISON_MODE, //
                debugParser, //
                fileSuffixes, //
                fixSimilarityThreshold(similarityThreshold), //
                maximumNumberOfComparisons, //
                DEFAULT_SIMILARITY_METRIC, //
                minimumTokenMatch, //
                exclusionFileName, //
                submissionDirectories, //
                oldSubmissionDirectories, //
                baseCodeSubmissionName, //
                subdirectoryName, //
                verbosity, //
                new ClusteringOptions.Builder().build() //
        );
    }

    public JPlagOptions withClusteringOptions(ClusteringOptions clusteringOptions) {
        return new JPlagOptions(languageOption, comparisonMode, debugParser, fileSuffixes, similarityThreshold, maximumNumberOfComparisons,
                similarityMetric, minimumTokenMatch, exclusionFileName, submissionDirectories, oldSubmissionDirectories, baseCodeSubmissionName,
                subdirectoryName, verbosity, clusteringOptions);
    }

    public JPlagOptions withMaximumNumberOfComparisons(int maximumNumberOfComparisons) {
        return new JPlagOptions(languageOption, comparisonMode, debugParser, fileSuffixes, similarityThreshold, maximumNumberOfComparisons,
                similarityMetric, minimumTokenMatch, exclusionFileName, submissionDirectories, oldSubmissionDirectories, baseCodeSubmissionName,
                subdirectoryName, verbosity, clusteringOptions);
    }

    public JPlagOptions withMinimumTokenMatch(int minimumTokenMatch) {
        return new JPlagOptions(languageOption, comparisonMode, debugParser, fileSuffixes, similarityThreshold, maximumNumberOfComparisons,
                similarityMetric, minimumTokenMatch, exclusionFileName, submissionDirectories, oldSubmissionDirectories, baseCodeSubmissionName,
                subdirectoryName, verbosity, clusteringOptions);
    }

    public JPlagOptions withLanguageOption(LanguageOption languageOption) {
        return new JPlagOptions(languageOption, comparisonMode, debugParser, fileSuffixes, similarityThreshold, maximumNumberOfComparisons,
                similarityMetric, minimumTokenMatch, exclusionFileName, submissionDirectories, oldSubmissionDirectories, baseCodeSubmissionName,
                subdirectoryName, verbosity, clusteringOptions);
    }

    public JPlagOptions withComparisonMode(ComparisonMode comparisonMode) {
        return new JPlagOptions(languageOption, comparisonMode, debugParser, fileSuffixes, similarityThreshold, maximumNumberOfComparisons,
                similarityMetric, minimumTokenMatch, exclusionFileName, submissionDirectories, oldSubmissionDirectories, baseCodeSubmissionName,
                subdirectoryName, verbosity, clusteringOptions);
    }

    public JPlagOptions withVerbosity(Verbosity verbosity) {
        return new JPlagOptions(languageOption, comparisonMode, debugParser, fileSuffixes, similarityThreshold, maximumNumberOfComparisons,
                similarityMetric, minimumTokenMatch, exclusionFileName, submissionDirectories, oldSubmissionDirectories, baseCodeSubmissionName,
                subdirectoryName, verbosity, clusteringOptions);
    }

    public JPlagOptions withExclusionFileName(String exclusionFileName) {
        return new JPlagOptions(languageOption, comparisonMode, debugParser, fileSuffixes, similarityThreshold, maximumNumberOfComparisons,
                similarityMetric, minimumTokenMatch, exclusionFileName, submissionDirectories, oldSubmissionDirectories, baseCodeSubmissionName,
                subdirectoryName, verbosity, clusteringOptions);
    }

    public JPlagOptions withBaseCodeSubmissionName(String baseCodeSubmissionName) {
        return new JPlagOptions(languageOption, comparisonMode, debugParser, fileSuffixes, similarityThreshold, maximumNumberOfComparisons,
                similarityMetric, minimumTokenMatch, exclusionFileName, submissionDirectories, oldSubmissionDirectories, baseCodeSubmissionName,
                subdirectoryName, verbosity, clusteringOptions);
    }

    public JPlagOptions withSubmissionDirectories(List<String> submissionDirectories) {
        return new JPlagOptions(languageOption, comparisonMode, debugParser, fileSuffixes, similarityThreshold, maximumNumberOfComparisons,
                similarityMetric, minimumTokenMatch, exclusionFileName, submissionDirectories, oldSubmissionDirectories, baseCodeSubmissionName,
                subdirectoryName, verbosity, clusteringOptions);
    }

    public JPlagOptions withDebugParser(boolean debugParser) {
        return new JPlagOptions(languageOption, comparisonMode, debugParser, fileSuffixes, similarityThreshold, maximumNumberOfComparisons,
                similarityMetric, minimumTokenMatch, exclusionFileName, submissionDirectories, oldSubmissionDirectories, baseCodeSubmissionName,
                subdirectoryName, verbosity, clusteringOptions);
    }

    public boolean hasBaseCode() {
        return baseCodeSubmissionName != null && !baseCodeSubmissionName.isBlank();
    }

    /**
     * Load Language used to parse the submissions.
     */
    public Language language() {
        // TODO DF: As soon as languages can be loaded via Service Loader API (or similar), no multiple instances will be
        // generated anymore.
        try {
            Constructor<?> constructor = Class.forName(languageOption.getClassPath()).getConstructor();
            Language language = (Language) constructor.newInstance();
            logger.info("Initialized language {}", language.getName());
            return language;
        } catch (NoSuchMethodException | SecurityException | ClassNotFoundException | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException e) {
            logger.error(e.getMessage(), e);
            return null;
        }

    }

    public Set<String> excludedFiles() {
        return Optional.ofNullable(exclusionFileName()).map(this::readExclusionFile).orElse(Collections.emptySet());
    }

    @Override
    public List<String> fileSuffixes() {
        var language = language();
        if (fileSuffixes == null && language != null)
            return Arrays.stream(language.suffixes()).toList();
        return fileSuffixes == null ? null : fileSuffixes.stream().toList();
    }

    /**
     * Path name of the directory containing the base code.<br>
     * For backwards compatibility it may also be a directory name inside the root directory. Condition for the latter is
     * <ul>
     * <li>Specified path does not exist.</li>
     * <li>Name has not have a separator character after trimming them from both ends (leaving at least a one-character
     * name).</li>
     * <li>A submission with the specified name exists in the root directory.</li>
     * </ul>
     * It's an error if a string has been provided but it is neither an existing path nor does it fulfill all the conditions
     * of the compatibility fallback listed above.
     */
    @Override
    public String baseCodeSubmissionName() {
        return (baseCodeSubmissionName == null || baseCodeSubmissionName.isBlank()) ? null : baseCodeSubmissionName;
    }

    @Override
    public Integer minimumTokenMatch() {
        Integer croppedMinimumTokenMatch = (minimumTokenMatch != null && minimumTokenMatch < 1) ? Integer.valueOf(1) : minimumTokenMatch;
        var language = language();
        if (croppedMinimumTokenMatch == null && language != null)
            return language.minimumTokenMatch();
        return minimumTokenMatch;
    }

    @Override
    public Integer maximumNumberOfComparisons() {
        return Math.max(maximumNumberOfComparisons, -1);
    }

    private Set<String> readExclusionFile(final String exclusionFileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(exclusionFileName, JPlagOptions.CHARSET))) {
            final var excludedFileNames = reader.lines().collect(Collectors.toSet());
            if (verbosity() == LONG) {
                logger.info("Excluded files:");
                for (var excludedFilename : excludedFileNames) {
                    logger.info(excludedFilename);
                }
            }
            return excludedFileNames;
        } catch (IOException e) {
            logger.error("Could not read exclusion file: " + e.getMessage(), e);
            return Collections.emptySet();
        }
    }

    private static float fixSimilarityThreshold(float similarityThreshold) {
        if (similarityThreshold > 100) {
            logger.warn("Maximum threshold of 100 used instead of {}", similarityThreshold);
            return 100;
        } else if (similarityThreshold < 0) {
            logger.warn("Minimum threshold of 0 used instead of {}", similarityThreshold);
            return 0;
        } else {
            return similarityThreshold;
        }
    }
}
