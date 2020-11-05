package org.jetbrains.skija.examples.lwjgl;

import java.text.*;
import java.util.*;
import java.util.stream.*;
import org.jetbrains.skija.*;
import org.jetbrains.skija.shaper.*;

public class RunIteratorScene implements Scene {
    public final Font lato36;
    public final Font inter36;
    public final Font inter11;
    public final FontMetrics inter11Metrics;
    public final Font geeza36;
    public final Paint boundsStroke = new Paint().setColor(0x403333CC).setMode(PaintMode.STROKE).setStrokeWidth(1);
    public final Paint boundsFill = new Paint().setColor(0x403333CC);
    public final Paint textFill = new Paint().setColor(0xFF000000);

    public RunIteratorScene() {
        lato36  = new Font(Typeface.makeFromFile("fonts/Lato-Regular.ttf"), 36);
        var inter = Typeface.makeFromFile("fonts/Inter-Regular.ttf");
        inter36 = new Font(inter, 36);
        inter11 = new Font(inter, 11);
        inter11Metrics = inter11.getMetrics();
        geeza36 = new Font(Typeface.makeFromFile("fonts/Geeza Pro Regular.ttf"), 36);
    }

    @Override
    public void draw(Canvas canvas, int width, int height, float dpi, int xpos, int ypos) {
        canvas.translate(20, 20);
        String text = "oneΩπΩдва😀🕵️‍♀️👩‍❤️‍👨ثلاثة1234خمسة";

        try (var shaper = Shaper.makeShapeThenWrap();) { // Shaper.makeCoreText();
            try (var handler = new DebugTextBlobHandler();)
            {
                var fontIter = new TrivialFontRunIterator(text.length(), lato36);
                var bidiIter = new TrivialBidiRunIterator(text.length(), Bidi.DIRECTION_LEFT_TO_RIGHT);
                var scriptIter = new TrivialScriptRunIterator(text.length(), "latn");
                var langIter = new TrivialLanguageRunIterator(text.length(), "en-US");
                shaper.shape(text, fontIter, bidiIter, scriptIter, langIter, null, width - 40, handler);
                drawBlob(canvas, handler, "All trivial");
            }

            try (var handler = new DebugTextBlobHandler();
                 var fontIter = new FontMgrRunIterator(text, lato36, null);)
            {
                var bidiIter = new TrivialBidiRunIterator(text.length(), Bidi.DIRECTION_LEFT_TO_RIGHT);
                var scriptIter = new TrivialScriptRunIterator(text.length(), "latn");
                var langIter = new TrivialLanguageRunIterator(text.length(), "en-US");
                shaper.shape(text, fontIter, bidiIter, scriptIter, langIter, null, width - 40, handler);
                drawBlob(canvas, handler, "FontMgrRunIterator");
            }

            try (var handler = new DebugTextBlobHandler();
                 var bidiIter = new IcuBidiRunIterator(text, Bidi.DIRECTION_LEFT_TO_RIGHT);)
            {
                var fontIter = new TrivialFontRunIterator(text.length(), lato36);
                var scriptIter = new TrivialScriptRunIterator(text.length(), "latn");
                var langIter = new TrivialLanguageRunIterator(text.length(), "en-US");
                shaper.shape(text, fontIter, bidiIter, scriptIter, langIter, null, width - 40, handler);
                drawBlob(canvas, handler, "IcuBidiRunIterator");
            }

            try (var handler = new DebugTextBlobHandler();)
            {
                var fontIter = new TrivialFontRunIterator(text.length(), lato36);
                var bidiIter = new JavaTextBidiRunIterator(text);
                var scriptIter = new TrivialScriptRunIterator(text.length(), "latn");
                var langIter = new TrivialLanguageRunIterator(text.length(), "en-US");
                shaper.shape(text, fontIter, bidiIter, scriptIter, langIter, null, width - 40, handler);
                drawBlob(canvas, handler, "JavaTextBidiRunIterator");
            }

            try (var handler = new DebugTextBlobHandler();
                 var scriptIter = new HbIcuScriptRunIterator(text);)
            {
                var fontIter = new TrivialFontRunIterator(text.length(), lato36);
                var bidiIter = new TrivialBidiRunIterator(text.length(), Bidi.DIRECTION_LEFT_TO_RIGHT);
                var langIter = new TrivialLanguageRunIterator(text.length(), "en-US");
                shaper.shape(text, fontIter, bidiIter, scriptIter, langIter, null, width - 40, handler);
                drawBlob(canvas, handler, "HbIcuScriptRunIterator");
            }

            try (var handler = new DebugTextBlobHandler();
                 var fontIter = new FontMgrRunIterator(text, lato36, null);
                 var bidiIter = new IcuBidiRunIterator(text, Bidi.DIRECTION_LEFT_TO_RIGHT);
                 var scriptIter = new HbIcuScriptRunIterator(text);)
            {
                var langIter = new TrivialLanguageRunIterator(text.length(), "en-US");
                shaper.shape(text, fontIter, bidiIter, scriptIter, langIter, null, width - 40, handler);
                drawBlob(canvas, handler, "All native");
            }
        }
    }

    private void drawBlob(Canvas canvas, DebugTextBlobHandler handler, String comment) {
        canvas.drawString(comment, 0, -inter11Metrics.getAscent(), inter11, textFill);
        canvas.translate(0, inter11Metrics.getHeight());

        try (var blob = handler._builder.build()) {
            canvas.drawTextBlob(blob, 0, 0, lato36, textFill);
        
            for (var pair: handler._infos) {
                var runBounds = pair.getSecond();
                canvas.drawRect(runBounds, boundsStroke);
            }

            canvas.translate(0, blob.getBounds().getBottom() + 20);
        }
    }
}
