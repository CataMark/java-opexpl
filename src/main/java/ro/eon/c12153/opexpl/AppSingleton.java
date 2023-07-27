package ro.any.c12153.opexpl;

import java.util.Optional;

/**
 *
 * @author catalin
 */
public class AppSingleton {
    public static final boolean CHILD_APP = Boolean.parseBoolean(Optional.ofNullable(System.getenv("CHILD_APP")).orElse("false"));
}
