import java.util.*;
import java.util.stream.Collectors;

public class SentimentAnalyzer {

    private Map<String, Integer> positiveWords;
    private Map<String, Integer> negativeWords;
    private Set<String> intensifiers;
    private Set<String> negations;

    public SentimentAnalyzer() {
        initializeWordDictionaries();
    }

    private void initializeWordDictionaries() {
        positiveWords = new HashMap<>();
        positiveWords.put("good", 2);
        positiveWords.put("great", 3);
        positiveWords.put("excellent", 4);
        positiveWords.put("amazing", 4);
        positiveWords.put("wonderful", 3);
        positiveWords.put("fantastic", 4);
        positiveWords.put("love", 3);
        positiveWords.put("like", 2);
        positiveWords.put("happy", 3);
        positiveWords.put("joy", 3);
        positiveWords.put("perfect", 4);
        positiveWords.put("best", 4);
        positiveWords.put("beautiful", 3);
        positiveWords.put("awesome", 4);
        positiveWords.put("nice", 2);
        positiveWords.put("enjoy", 2);
        positiveWords.put("pleased", 2);
        positiveWords.put("delighted", 3);

        negativeWords = new HashMap<>();
        negativeWords.put("bad", -2);
        negativeWords.put("terrible", -4);
        negativeWords.put("awful", -4);
        negativeWords.put("horrible", -4);
        negativeWords.put("hate", -3);
        negativeWords.put("dislike", -2);
        negativeWords.put("sad", -3);
        negativeWords.put("worst", -4);
        negativeWords.put("poor", -2);
        negativeWords.put("disappointing", -3);
        negativeWords.put("disappointed", -3);
        negativeWords.put("ugly", -3);
        negativeWords.put("disgusting", -4);
        negativeWords.put("annoying", -2);
        negativeWords.put("boring", -2);
        negativeWords.put("useless", -3);
        negativeWords.put("waste", -3);

        intensifiers = new HashSet<>(Arrays.asList(
                "very", "extremely", "absolutely", "really", "so", "quite", "incredibly"
        ));

        negations = new HashSet<>(Arrays.asList(
                "not", "no", "never", "neither", "nobody", "nothing", "nowhere", "doesn't", "don't", "didn't"
        ));
    }

    public SentimentResult analyze(String text) {
        if (text == null || text.trim().isEmpty()) {
            return new SentimentResult(0, 0, 0, 0, "NEUTRAL");
        }

        String processedText = text.toLowerCase().replaceAll("[^a-z\\s]", " ");
        List<String> words = Arrays.stream(processedText.split("\\s+"))
                .filter(w -> !w.isEmpty())
                .collect(Collectors.toList());

        int totalScore = 0;
        int positiveCount = 0;
        int negativeCount = 0;

        for (int i = 0; i < words.size(); i++) {
            String word = words.get(i);
            int score = 0;

            if (positiveWords.containsKey(word)) {
                score = positiveWords.get(word);
                positiveCount++;
            } else if (negativeWords.containsKey(word)) {
                score = negativeWords.get(word);
                negativeCount++;
            }

            if (score != 0 && i > 0 && intensifiers.contains(words.get(i - 1))) {
                score = (int) (score * 1.5);
            }

            if (score != 0) {
                boolean isNegated = false;
                if (i > 0 && negations.contains(words.get(i - 1))) {
                    isNegated = true;
                } else if (i > 1 && negations.contains(words.get(i - 2))) {
                    isNegated = true;
                }

                if (isNegated) {
                    score = -score;
                    if (score > 0) {
                        negativeCount--;
                        positiveCount++;
                    } else {
                        positiveCount--;
                        negativeCount++;
                    }
                }
            }

            totalScore += score;
        }

        String sentiment = determineSentiment(totalScore);

        return new SentimentResult(totalScore, positiveCount, negativeCount, words.size(), sentiment);
    }

    private String determineSentiment(int score) {
        if (score >= 5) return "VERY POSITIVE";
        else if (score >= 2) return "POSITIVE";
        else if (score <= -5) return "VERY NEGATIVE";
        else if (score <= -2) return "NEGATIVE";
        else return "NEUTRAL";
    }

    public void analyzeAndPrint(String text) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("Analyzing: \"" + text + "\"");
        System.out.println("=".repeat(60));

        SentimentResult result = analyze(text);
        System.out.println(result);
    }

    static class SentimentResult {
        private int score;
        private int positiveWordCount;
        private int negativeWordCount;
        private int totalWords;
        private String sentiment;

        public SentimentResult(int score, int positiveWordCount, int negativeWordCount,
                               int totalWords, String sentiment) {
            this.score = score;
            this.positiveWordCount = positiveWordCount;
            this.negativeWordCount = negativeWordCount;
            this.totalWords = totalWords;
            this.sentiment = sentiment;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Sentiment Analysis Results:\n");
            sb.append("-".repeat(60)).append("\n");
            sb.append(String.format("Overall Sentiment: %s\n", sentiment));
            sb.append(String.format("Sentiment Score: %d\n", score));
            sb.append(String.format("Positive Words Found: %d\n", positiveWordCount));
            sb.append(String.format("Negative Words Found: %d\n", negativeWordCount));
            sb.append(String.format("Total Words Analyzed: %d\n", totalWords));

            double positivePercent = totalWords > 0 ? (positiveWordCount * 100.0 / totalWords) : 0;
            double negativePercent = totalWords > 0 ? (negativeWordCount * 100.0 / totalWords) : 0;

            sb.append(String.format("Positive Word Ratio: %.2f%%\n", positivePercent));
            sb.append(String.format("Negative Word Ratio: %.2f%%\n", negativePercent));
            sb.append("-".repeat(60));

            return sb.toString();
        }

        public int getScore() { return score; }
        public String getSentiment() { return sentiment; }
        public int getPositiveWordCount() { return positiveWordCount; }
        public int getNegativeWordCount() { return negativeWordCount; }
    }

    public static void main(String[] args) {
        SentimentAnalyzer analyzer = new SentimentAnalyzer();

        String[] testTexts = {
                "I love this product! It's absolutely amazing and wonderful.",
                "This is terrible. I hate it so much. Worst purchase ever!",
                "The movie was okay. Nothing special but not bad either.",
                "I'm not happy with this service. It's disappointing.",
                "This is not bad at all! Actually quite good.",
                "Very excellent performance! The best I've ever seen.",
                "What a waste of time. Boring and useless."
        };

        System.out.println("TEXT SENTIMENT ANALYZER");
        System.out.println("=".repeat(60));

        for (String text : testTexts) {
            analyzer.analyzeAndPrint(text);
        }

        Scanner scanner = new Scanner(System.in);
        System.out.println("\n\n" + "=".repeat(60));
        System.out.println("INTERACTIVE MODE - Enter your own text (or 'quit' to exit)");
        System.out.println("=".repeat(60));

        while (true) {
            System.out.print("\nEnter text to analyze: ");
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("quit") || input.equalsIgnoreCase("exit")) {
                System.out.println("Thank you for using Text Sentiment Analyzer!");
                break;
            }

            if (!input.trim().isEmpty()) {
                analyzer.analyzeAndPrint(input);
            }
        }

        scanner.close();
    }
}
