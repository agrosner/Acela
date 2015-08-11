package com.andrewgrosner.acela.test.keynamerules;

import com.andrewgrosner.acela.annotation.Key;
import com.andrewgrosner.acela.annotation.Translatable;

/**
 * Description:
 */
@Translatable(keyNameRule = Translatable.KeyNameRule.UNDERSCORE_AND_LOWERCASE)
public class UnderScore {

    @Key
    String nameIsHardToDecipher;

    @Key
    int NameIsBetter;
}
