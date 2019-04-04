package com.isluji.travial.dummy;

import androidx.annotation.NonNull;

import com.isluji.travial.model.Trivia;
import com.isluji.travial.model.TriviaAnswer;
import com.isluji.travial.model.TriviaQuestion;

import java.util.ArrayList;
import java.util.List;

/**
 * Usar para generar datos para probar la interfaz
 * (hasta que implemente la base de datos)
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<Trivia> TRIVIAS = new ArrayList<>();

    /**
     * A map of sample (dummy) items, by ID.
     */
//    public static final Map<String, DummyItem> ITEM_MAP = new HashMap<String, DummyItem>();

    private static final int COUNT = 5;

    // Add some sample items.
    static {

        // Create sample Trivia items
        for (int i = 1; i <= COUNT; i++) {
            Trivia t = new Trivia(/* TODO */);

            // Add sample Questions to the Trivias
            for (int j = 1; j <= COUNT; j++) {
                TriviaQuestion tq = new TriviaQuestion(/* TODO */);
                // Add sample Answers to the Questions
                for (int k = 1; k <= COUNT; k++) {
                    boolean correct = (k % 2 == 0);
                    TriviaAnswer ta = new TriviaAnswer(/* TODO */);
                    tq.addAnswer(ta);
                }

                t.addQuestion(tq);
            }

            TRIVIAS.add(t);
        }

    }

    // --------------------------------------------------------------

    private static void addItem(DummyItem item) {
//        TRIVIAS.add(item);
//        ITEM_MAP.put(item.id, item);
    }

    @NonNull
    private static DummyItem createDummyItem(int position) {
        return new DummyItem(String.valueOf(position), "Item " + position, makeDetails(position));
    }

    @NonNull
    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);

        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }

        return builder.toString();
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class DummyItem {
        public final String id;
        public final String content;
        public final String details;

        public DummyItem(String id, String content, String details) {
            this.id = id;
            this.content = content;
            this.details = details;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
