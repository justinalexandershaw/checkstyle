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
     * Represents the tab-width of the indentation level that the user chose to use. As per the
     * style guide, we suggest the user use either 3 or 4 but we do not care which choice they
     * make. This will keep track of their choice
     */
    private static Integer chosenIndentationLevel;

    public static void setChosenIndentationLevel(int chosenLevel) {
        chosenIndentationLevel = chosenLevel;
    }

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
     * Creates new instance with a list of acceptable indentation levels.
     *
     * @param indents set of acceptable indentation levels (ie. either 3 or 4 spaces).
     */
    public IndentLevel(int... indentationLevels) {
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
    public IndentLevel(IndentLevel base, Set<Integer> acceptableOffsets) {
        if (base.acceptableIndents.size() != acceptableOffsets.size()) {
            throw new IllegalArgumentException();
        }
        for (int offset : acceptableOffsets) {
            final BitSet src = base.acceptableIndents.get(offset);
            int indexOfSetBit = src.nextSetBit(0); // returns -1 if not found
            while (indexOfSetBit > -1) {
                this.acceptableIndents.get(offset).set(indexOfSetBit + offset);
                indexOfSetBit = src.nextSetBit(indexOfSetBit + 1);
            }
        }
    }

    /**
     * Creates new instance with no acceptable indentation level.
     * This is only used internally to combine multiple levels.
     */
    private IndentLevel() {
        // implementation is intentionally left blank
    }

    /**
     * Checks whether we have more than one level of acceptable indentation places. If the
     * given indentation level has not been chosen yet, then we return a logical OR search of
     * all the BitSets related to this object. If any of those have multiple levels then this
     * method will return true.
     *
     * @return whether we have more than one level.
     */
    public final boolean isMultiLevel() {
        if (chosenIndentationLevel != null) {
            return this.acceptableIndents.get(chosenIndentationLevel).cardinality() > 1;
        }
        for (BitSet bitSet : this.acceptableIndents.values()) {
            if (bitSet.cardinality() > 1) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if given indentation is acceptable. If there has already been a chosen indentation
     * level the we check if the given indentation is acceptable to that indentation level choice.
     * Otherwise, we check all the available indentation levels, the first one that we find which
     * is acceptable we will mark as being the chosen indentation level.
     *
     * @param indent indentation to check.
     * @return true if given indentation is acceptable, false otherwise.
     */
    public boolean isAcceptable(int indent) {
        if (chosenIndentationLevel != null) {
            return this.acceptableIndents.get(chosenIndentationLevel).get(indent);
        }
        for (BitSet bitSet : this.acceptableIndents.values()) {
            if (bitSet.get(indent)) {
                IndentLevel.setChosenIndentationLevel(indent);
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if indent less than minimal of acceptable indentation levels, false otherwise.
     *
     * @param indent indentation to check.
     * @return true if {@code indent} less than minimal of acceptable indentation levels, and
     * false otherwise.
     */
    public boolean isGreaterThan(int indent) {
        if (chosenIndentationLevel != null) {
            return this.acceptableIndents.get(chosenIndentationLevel).nextSetBit(0) > indent;
        }
        for (BitSet bitSet : this.acceptableIndents.values()) {
            if (bitSet.nextSetBit(0) > indent) {
                return true;
            }
        }
        return false;
    }

    /**
     * Creates a new indentation level which is a combination of one or more acceptable
     * indentation levels.
     *
     * @param base class to add new indentations to.
     * @param additions new acceptable indentation.
     * @return New acceptable indentation level instance.
     */
    public static IndentLevel addAcceptable(IndentLevel base, int... additions) {
        final IndentLevel result = new IndentLevel();
        if (chosenIndentationLevel != null) {
            final BitSet baseBitSet = base.acceptableIndents.get(chosenIndentationLevel);
            BitSet newBitSet = createNewBitSetFromBaseAndAdditions(baseBitSet, additions);
            result.acceptableIndents.putIfAbsent(chosenIndentationLevel, newBitSet);
        } else {
            for (int key : base.acceptableIndents.keySet()) {
                final BitSet baseBitSet = base.acceptableIndents.get(key);
                BitSet newBitSet = createNewBitSetFromBaseAndAdditions(baseBitSet, additions);
                result.acceptableIndents.putIfAbsent(key, newBitSet);
            }
        }
        return result;
    }

    /**
     * Creates a new BitSet which is the logical OR of the base and the additions.
     * @param baseBitSet the core bitset from which to construct the new BitSet
     * @param additions the additional indentation levels to add to the base level
     * @return the new bitset created by combining the base and additions
     */
    private static BitSet createNewBitSetFromBaseAndAdditions(BitSet baseBitSet, int... additions) {
        BitSet newBitSet = new BitSet();
        newBitSet.or(baseBitSet);
        for (int addition : additions) {
            newBitSet.set(addition); // adds all elements of additions to each value in map
        }
        return newBitSet;
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
        if (chosenIndentationLevel != null) {
            final BitSet newBitSet = createNewBitSetFromIndents(base, addition, 
                                                                chosenIndentationLevel);
            result.acceptableIndents.putIfAbsent(chosenIndentationLevel, newBitSet);
        } else {
            for (int key : base.acceptableIndents.keySet()) {
                final BitSet newBitSet = createNewBitSetFromIndents(base, addition, key);
                result.acceptableIndents.putIfAbsent(key, newBitSet);
            }
        }
        return result;
    }

    private static BitSet createNewBitSetFromIndents(IndentLevel base, IndentLevel addition, 
                                                     int index) {
        final BitSet newBitSet = new BitSet();
        final BitSet baseBitSet = base.acceptableIndents.get(index);
        final BitSet additionBitSet = addition.acceptableIndents.get(index);
        newBitSet.or(baseBitSet);
        newBitSet.or(additionBitSet);
        return newBitSet;
    }

    /**
     * Returns first indentation level of the chosen indentation tab-width if the user has chosen
     * a certain indent level. Otherwise, returns the shortest indent level from all the acceptable
     * levels.
     *
     * @return indentation level.
     */
    public int getFirstIndentLevel() {
        if (chosenIndentationLevel != null) {
            return this.acceptableIndents.get(chosenIndentationLevel).nextSetBit(0);
        }
        int firstIndentLevel = Integer.MAX_VALUE;
        for (BitSet bitSet : this.acceptableIndents.values()) {
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
        if (chosenIndentationLevel != null) {
            return this.acceptableIndents.get(chosenIndentationLevel).length() - 1;
        }
        int lastIndentLevel = Integer.MIN_VALUE;
        for (BitSet bitSet : this.acceptableIndents.values()) {
            lastIndentLevel = Math.max(lastIndentLevel, bitSet.length() - 1);
        }
        return lastIndentLevel;
    }

    @Override
    public String toString() {
        if (chosenIndentationLevel == null) {
            return "Default Indent Level";
        }
        final String result;
        final BitSet bitSet = this.acceptableIndents.get(chosenIndentationLevel);
        if (bitSet.cardinality() == 1) {
            result = String.valueOf(bitSet.nextSetBit(0));
        }
        else {
            final StringBuilder sb = new StringBuilder(50);
            for (int i = bitSet.nextSetBit(0); i >= 0;
                 i = bitSet.nextSetBit(i + 1)) {
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
