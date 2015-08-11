package com.andrewgrosner.acela.test.keynamerules;

import com.andrewgrosner.acela.annotation.Key;
import com.andrewgrosner.acela.annotation.Translatable;

/**
 * Description:
 */
@Translatable(keyNameRule = Translatable.KeyNameRule.LOWERCASE)
public class LowerCase {

    @Key
    String Lowercaser;

    @Key
    int DontWorryAboutMe;
}
