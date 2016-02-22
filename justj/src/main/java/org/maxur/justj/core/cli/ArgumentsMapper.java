package org.maxur.justj.core.cli;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>21.02.2016</pre>
 */
public interface ArgumentsMapper<O> {

    O readValue(String[] args) throws OptionsProcessingException;

}
