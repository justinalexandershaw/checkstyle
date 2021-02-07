package edu.uw.cs.checks.readability.indentation;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * <p>
 * Encapsulates the idea of expected indentation levels.
 * </p>
 * <p>
 * We want to provide a way for the user to either write code with n spaces or m spaces
 * and either choice is okay so long as they are consistent with their choice. Within a 
 * given choice, there are multiple acceptable places for some lines to be indented and
 * we represent this with a BitSet object which essentially is just a list of positional
 * booleans describing the acceptabiliy to indent the line to that position.
 * </p>
 * <p>
 * You can think of this object as an "indentation guru" which you can ask if a certain
 * indentation level is acceptable or not. For example, "is four spaces acceptable?" might
 * return the result "true", but than "is five spaces acceptable?" might return false.
 * </p>
 */
public class IndentLevel {

    /** 
     * Map of acceptable indentation levels.
     * 
     * As per the style guide, we do not care whether students use three or four spaces, so long
     * as they are consistent with that choice. This map holds several (typically two) indentation
     * levels which are acceptable and those integer indentation levels map to their own BitSet
     * each of which correspond to the set of possible indentation choices for a tab-width of
     * some number. Typically this will be three and four.
     */
    private final Map<Integer, BitSet> acceptableIndents = new HashMap<>();

    /**
     * Creates new instance with a set of acceptable indentation levels.
     *
     * @param indents set of acceptable indentation levels (ie. either 3 or 4 spaces).
     */
    public IndentLevel(Set<Integer> indentationLevels) {
        for (int indentationLevel : indentationLevels) {
            BitSet newBitSet = new BitSet();
            newBitSet.set(indentationLevel);
            acceptableIndents.putIfAbsent(indentationLevel, newBitSet);
        }
    }

    /**
     * Adds the acceptable offsets to their coresponding parent indentation level.
     *
     * @param base parent's level
     * @param offsets offsets from parent's level (ie. either 3 or 4 spaces).
     */
    public IndentLevel(IndentLevel baseIndentLevel, Set<Integer> acceptableOffsets) {
        
        // get the base indentation bitset
        // while there is a next set bit in the base indentation level
            // add the corresponding offset to this bitset's acceptableIndentation levles

        // this code is so ugly omg
        // for (BitSet srcBitSet : baseIndentLevel.levels) {
        //     for (int i = srcBitSet.nextSetBit(0); i >= 0; i = srcBitSet.nextSetBit(i + 1)) {
        //         for (int offset : acceptableOffsets) {
        //             for (BitSet bitSet : levels) {
        //                 bitSet.set(i + offset);
        //             }
        //         }
        //     }
        // }
        // final BitSet src = base.levels;
        // for (int i = src.nextSetBit(0); i >= 0; i = src.nextSetBit(i + 1)) {
        //     for (int offset : offsets) {
        //         levels.set(i + offset);
        //     }
        // }
    }

    /**
     * Creates new instance with no acceptable indentation level.
     * This is only used internally to combine multiple levels.
     */
    private IndentLevel() {
        // implementation is intentionally left blank
    }

    /**
     * Checks whether we have more than one level.
     *
     * @return whether we have more than one level.
     */
    public final boolean isMultiLevel() {
        for (BitSet bitSet : indentationLevels) {
            if (bitSet.cardinality() > 1) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if given indentation is acceptable.
     *
     * @param indent indentation to check.
     * @return true if given indentation is acceptable,
     *         false otherwise.
     */
    public boolean isAcceptable(int indent) {
        for (BitSet bitSet : levels) {
            if (bitSet.get(indent)) {
                levels.clear();
                levels.add(bitSet);
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if indent less than minimal of
     * acceptable indentation levels, false otherwise.
     *
     * @param indent indentation to check.
     * @return true if {@code indent} less than minimal of
     *         acceptable indentation levels, false otherwise.
     */
    public boolean isGreaterThan(int indent) {
        for (BitSet bitSet : levels) {
            if (bitSet.nextSetBit(0) > indent) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds one or more acceptable indentation level.
     *
     * @param base class to add new indentations to.
     * @param additions new acceptable indentation.
     * @return New acceptable indentation level instance.
     */
    public static IndentLevel addAcceptable(IndentLevel base, int... additions) {
        final IndentLevel result = new IndentLevel();
        for (BitSet resultBitSet : result.levels) {
            for (BitSet baseBitSet : base.levels) {
                resultBitSet.or(baseBitSet);
            }
        }
        for (BitSet resultBitSet : result.levels) {
            for (int addition : additions) {
                resultBitSet.set(addition);
            }
        }
        return result;
    }

    /**
     * Combines 2 acceptable indentation level classes.
     *
     * @param base class to add new indentations to.
     * @param addition new acceptable indentation.
     * @return New acceptable indentation level instance.
     */
    public static IndentLevel addAcceptable(IndentLevel base, IndentLevel addition) {
        final IndentLevel result = new IndentLevel();
        result.levels.or(base.levels);
        result.levels.or(addition.levels);
        return result;
    }

    /**
     * Returns first indentation level.
     *
     * @return indentation level.
     */
    public int getFirstIndentLevel() {
        int firstIndentLevel = Integer.MAX_VALUE;
        for (BitSet bitSet : levels) {
            firstIndentLevel = Math.min(firstIndentLevel, bitSet.nextSetBit(0));
        }
        return firstIndentLevel;
    }

    /**
     * Returns last indentation level.
     *
     * @return indentation level.
     */
    public int getLastIndentLevel() {
        int lastIndentLevel = Integer.MIN_VALUE;
        for (BitSet bitSet : levels) {
            lastIndentLevel = Math.max(lastIndentLevel, bitSet.length() - 1);
        }
        return lastIndentLevel;
    }

    @Override
    public String toString() {
        final String result;
        if (levels.cardinality() == 1) {
            result = String.valueOf(levels.nextSetBit(0));
        }
        else {
            final StringBuilder sb = new StringBuilder(50);
            for (int i = levels.nextSetBit(0); i >= 0;
                 i = levels.nextSetBit(i + 1)) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append(i);
            }
            result = sb.toString();
        }
        return result;
    }
}
