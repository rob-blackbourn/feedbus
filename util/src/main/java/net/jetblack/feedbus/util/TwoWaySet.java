package net.jetblack.feedbus.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A set which maps from either of two values.
 * @param <TFirst> The type of the first value.
 * @param <TSecond> The type of the second value.
 */
public class TwoWaySet<TFirst, TSecond> {

    private final Map<TFirst, Set<TSecond>> _firstToSeconds = new HashMap<TFirst, Set<TSecond>>();
    private final Map<TSecond, Set<TFirst>> _secondToFirsts = new HashMap<TSecond, Set<TFirst>>();

    /**
     * Adds the two values.
     * @param first The first value.
     * @param second The second value.
     */
    public void addFirstAndSecond(TFirst first, TSecond second) {
        Set<TSecond> seconds = _firstToSeconds.get(first);
        if (seconds == null) {
            _firstToSeconds.put(first, seconds = new HashSet<TSecond>());
        }
        seconds.add(second);

        Set<TFirst> firsts = _secondToFirsts.get(second);
        if (firsts == null) {
            _secondToFirsts.put(second, firsts = new HashSet<TFirst>());
        }
        firsts.add(first);
    }

    /**
     * Adds the two values.
     * @param second The second value.
     * @param first The first value.
     */
    public void addSecondAndFirst(TSecond second, TFirst first) {
        addFirstAndSecond(first, second);
    }

    /**
     * Determines if the first value is in the set.
     * @param first The first value.
     * @return true if the first value is no the set.
     */
    public boolean containsFirstKey(TFirst first) {
        return _firstToSeconds.containsKey(first);
    }

    /**
     * Determines if the second value is in the set.
     * @param second The second value.
     * @return true if the second value is in the set.
     */
    public boolean containsSecondKey(TSecond second) {
        return _secondToFirsts.containsKey(second);
    }

    /**
     * Gets the second value that is mapped to the first value.
     * @param first The first value.
     * @return The second value if found; otherwise null.
     */
    public Set<TSecond> getSecondFromFirst(TFirst first) {
        return _firstToSeconds.get(first);
    }

    /**
     * Gets the set of first values that are mapped to the second value.
     * @param second The second value.
     * @param firsts The set of first values.
     * @return The set of matching values.
     */
    public Set<TFirst> getFirstFromSecond(TSecond second, Set<TFirst> firsts) {
        return _secondToFirsts.get(second);
    }

    /**
     * Remove all mappings which match the first value.
     * @param first The first value.
     * @return The set without the first values.
     */
    public Set<TSecond> removeFirst(TFirst first) {
        Set<TSecond> seconds = _firstToSeconds.get(first);
        if (seconds == null) {
            return null;
        }

        Set<TSecond> secondsWithoutFirsts = new HashSet<TSecond>();

        for (TSecond second : seconds) {
            Set<TFirst> firsts = _secondToFirsts.get(second);
            firsts.remove(first);
            if (firsts.size() == 0) {
                _secondToFirsts.remove(second);
                secondsWithoutFirsts.add(second);
            }
        }

        _firstToSeconds.remove(first);

        return secondsWithoutFirsts;
    }

    /**
     * Remove all mappings which match the second value.
     * @param second The second value.
     * @return The set without the second values.
     */
    public Set<TFirst> removeSecond(TSecond second)
    {
        Set<TFirst> firsts = _secondToFirsts.get(second);
        if (firsts == null) {
            return null;
        }

        Set<TFirst> firstsWithoutSeconds = new HashSet<TFirst>();

        for (TFirst first : firsts) {
            Set<TSecond> seconds = _firstToSeconds.get(first);
            seconds.remove(second);
            if (seconds.size() == 0) {
                _firstToSeconds.remove(first);
                firstsWithoutSeconds.add(first);
            }
        }

        _secondToFirsts.remove(second);

        return firstsWithoutSeconds;
    }

    /**
     * Gets all the first values.
     * @return All the first values.
     */
    public Set<TFirst> getFirsts() {
    	return _firstToSeconds.keySet();
    }

    /**
     * Gets all the second values.
     * @return All the second values.
     */
    public Set<TSecond> getSeconds() {
    	return _secondToFirsts.keySet();
    }
    
}
