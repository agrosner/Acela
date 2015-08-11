package com.andrewgrosner.acela.test.mergeable;

import com.andrewgrosner.acela.annotation.Key;
import com.andrewgrosner.acela.annotation.Mergeable;
import com.andrewgrosner.acela.annotation.NotMergeable;
import com.andrewgrosner.acela.annotation.Translatable;

import java.util.Date;

/**
 * Description:
 */
@Translatable
@Mergeable
public class MergeableObject {

    @Key
    String name;

    @Key
    Date date;

    @Key
    @NotMergeable
    String notMergeable;
}
