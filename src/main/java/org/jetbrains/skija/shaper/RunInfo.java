package org.jetbrains.skija.shaper;

import lombok.*;
import org.jetbrains.annotations.*;
import org.jetbrains.skija.*;
import org.jetbrains.skija.impl.*;

@lombok.Data
public class RunInfo implements AutoCloseable {
    public final Font  _font;
    public final int   _bidiLevel;
    public final float _advanceX;
    public final float _advanceY;
    public final long  _glyphCount;
    // TODO convert to UTF-16 range?
    public final long  _utf8RangeBegin;
    public final long  _utf8RangeSize;

    public RunInfo(long fontPtr, int bidiLevel, float advanceX, float advanceY, long glyphCount, long utf8RangeBegin, long utf8RangeSize) {
        _font = new Font(fontPtr);
        _bidiLevel = bidiLevel;
        _advanceX = advanceX;
        _advanceY = advanceY;
        _glyphCount = glyphCount;
        _utf8RangeBegin = utf8RangeBegin;
        _utf8RangeSize = utf8RangeSize;
    }

    public Point getAdvance() {
        return new Point(_advanceX, _advanceY);
    }

    public long getUtf8RangeEnd() {
        return _utf8RangeBegin + _utf8RangeSize;
    }

    @Override
    public void close() {
        _font.close();
    }
}
