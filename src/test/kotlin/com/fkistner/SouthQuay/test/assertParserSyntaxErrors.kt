package com.fkistner.SouthQuay.test

import com.fkistner.SouthQuay.grammar.SQLangParser
import org.junit.Assert

/** Assert that parser has detected syntax errors */
internal fun assertParserSyntaxErrors(parser: SQLangParser) {
    Assert.assertTrue("Parser should have syntax errors.", parser.numberOfSyntaxErrors > 0)
}

/** Assert that parser was able to parse source without errors */
internal fun assertNoParserSyntaxErrors(parser: SQLangParser) {
    Assert.assertEquals("Parser should not have syntax errors.", 0, parser.numberOfSyntaxErrors)
}
