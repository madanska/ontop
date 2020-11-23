/*
 * #%L
 * ontop-obdalib-core
 * %%
 * Copyright (C) 2009 - 2014 Free University of Bozen-Bolzano
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

/*
 This grammar is adapted from https://github.com/antlr/grammars-v4/tree/master/turtle,
 derived in turn from http://www.w3.org/TR/turtle/#sec-grammar-grammar,
 with the following copywright:

 [The "BSD licence"]
 Copyright (c) 2014, Alejandro Medrano (@ Universidad Politecnica de Madrid, http://www.upm.es/)
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:
 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
    derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
*/



grammar TurtleOBDA;

 /*
 Source files (Parser, Visitor, ...) are generated by the ANTLR4 Maven Plugin,
 during the Maven generate-sources phase.
 If src/main/<subPath>/TurtleOBDA.g4 is the path to this file,
 then the source files are generated in target/generated-sources/antlr4/<subPath>
 */


/*------------------------------------------------------------------
 * PARSER RULES
 *------------------------------------------------------------------*/

parse
  : (triplesStatement|quadsStatement)+ EOF
  ;

triplesStatement
  : triples '.'
  ;

quadsStatement
    : 'GRAPH' graph '{' triplesStatement+ '}'
    ;

triples
  : subject  predicateObjectList
  // blankNodePropertyList predicateObjectList?
  ;

predicateObjectList
  : predicateObject (';' predicateObject)*
  ;

predicateObject
  : verb objectList
  ;

objectList // ok
  : object (',' object)*
  ;

verb
  : resource  // predicate = iri
  | 'a'
  ;

graph
  : resource
  | blank
  | variable   // treated as rr:column
  ;

subject    // iri, BlankNode, collection
  : resource
  | blank
  | variable   // treated as rr:column
  ;

object   // iri, BlankNode, collection, blankNodePropertyList, literal
  : resource
  | blank
  | literal
  | variableLiteral
  ;

resource
  : iri
  | IRIREF_WITH_PLACEHOLDERS
  | PREFIXED_NAME_WITH_PLACEHOLDERS
  ;

blank
  : BLANK_NODE_LABEL
  | BLANK_NODE_LABEL_WITH_PLACEHOLDERS // addition
  | ANON
  ;

variable
  : PLACEHOLDER
  ;

variableLiteral
  : PLACEHOLDER (LANGTAG | '^^' iri)?
  ;

iri
   : IRIREF
   | PREFIXED_NAME
   ;

literal // ok
  : rdfLiteral
  | numericLiteral
  | booleanLiteral
  ;

rdfLiteral  // ok
  : litString (LANGTAG | '^^' iri)?
  ;

litString
  : STRING_LITERAL_QUOTE
//  | STRING_LITERAL_SINGLE_QUOTE | STRING_LITERAL_LONG_SINGLE_QUOTE | STRING_LITERAL_LONG_QUOTE
  ;

booleanLiteral  // only the first two are in Turtle
  : 'true' | 'false'| 'TRUE' | 'True' | 'FALSE'| 'False'
  ;

numericLiteral  // ok
  : INTEGER | DOUBLE | DECIMAL
  ;

WS
  : ([\t\r\n\u000C] | ' ') + -> skip
  ;

/*------------------------------------------------------------------
 * LEXER RULES
 Applied for tokenization (before parsing), regardless of parser rules, as follows:
 - The rule matching the longest substring is applied
 - If there are several of them, the first one is applied
 *------------------------------------------------------------------*/

PLACEHOLDER
  : '{' ~[{}]+ '}'
  | '{"' (~["] | '""')+ '"}'
  ;

IRIREF_WITH_PLACEHOLDERS
  : '<' IRIREF_INNER_CHAR* (PLACEHOLDER IRIREF_INNER_CHAR*)+ '>'
  ;

IRIREF
   : '<' IRIREF_INNER_CHAR* '>'
   ;

PNAME_NS // ok
  : PN_PREFIX? ':'
  ;

PN_PREFIX // ok
   : PN_CHARS_BASE ((PN_CHARS | '.')* PN_CHARS)?
   ;

PREFIXED_NAME // PNAME_LN
   : PNAME_NS PN_LOCAL
  ;

// PrefixedName is PNAME_LN or PNAME_NS
PREFIXED_NAME_WITH_PLACEHOLDERS
  : PNAME_NS PN_LOCAL_WITH_PLACEHOLDERS
  ;

BLANK_NODE_LABEL_WITH_PLACEHOLDERS
  : '_:'  PN_LOCAL_WITH_PLACEHOLDERS
  ;

// The characters _ and digits may appear anywhere in a blank node label.
// The character . may appear anywhere except the first or last character.
// The characters -, U+00B7, U+0300 to U+036F and U+203F to U+2040 are permitted anywhere except the first character.
BLANK_NODE_LABEL // ok
  : '_:' (PN_CHARS_U | [0-9]) ((PN_CHARS | '.')* PN_CHARS)?
  ;

/* LANGTAG */

LANGTAG // ok
  : '@' [a-zA-Z] + ('-' [a-zA-Z0-9] +)*
  ;

/* NUMERIC LITERALS: START */

INTEGER // ok
  : [+-]? [0-9] +
  ;

DECIMAL // ok
  : [+-]? [0-9]* '.' [0-9] +
  ;

DOUBLE // ok
  : [+-]? ([0-9] + '.' [0-9]* EXPONENT | '.' [0-9] + EXPONENT | [0-9] + EXPONENT)
  ;

EXPONENT // ok
  : [eE] [+-]? [0-9] +
  ;

/* NUMERIC LITERALS: END */

// TURTLE.g4 says  '"' (~ ["\\\r\n] | '\'' | '\\"')* '"'
// but the one below is what is written in https://www.w3.org/TR/turtle/#grammar-production-STRING_LITERAL_QUOTE
STRING_LITERAL_QUOTE
  : '"' (~ ["\\\r\n] | ECHAR |  UCHAR)* '"'
  ;

UCHAR // ok, numeric escapes for IRIs and Strings
  : '\\u' HEX HEX HEX HEX | '\\U' HEX HEX HEX HEX HEX HEX HEX HEX
  ;

ECHAR // ok, string escapes for Strings only
  : '\\' [tbnrf"'\\]
  ;

ANON_WS // ok
  : ' ' | '\t' | '\r' | '\n'
  ;

ANON // ok
  : '[' ANON_WS* ']'
  ;

PN_CHARS_BASE // ok
  : [A-Z] | [a-z] | [\u00C0-\u00D6] | [\u00D8-\u00F6] | [\u00F8-\u02FF] | [\u0370-\u037D] |
  [\u037F-\u1FFF] | [\u200C-\u200D] | [\u2070-\u218F] | [\u2C00-\u2FEF] | [\u3001-\uD7FF] |
    [\uF900-\uFDCF] | [\uFDF0-\uFFFD]
// Limitation: Unicode Characters beyond \uFFFF are not (yet?) supported by ANTLR
//    | '\u10000' .. '\u1FFFD' | '\u20000' .. '\u2FFFD' |
//    '\u30000' .. '\u3FFFD' | '\u40000' .. '\u4FFFD' | '\u50000' .. '\u5FFFD' | '\u60000' .. '\u6FFFD' |
//    '\u70000' .. '\u7FFFD' | '\u80000' .. '\u8FFFD' | '\u90000' .. '\u9FFFD' | '\uA0000' .. '\uAFFFD' |
//    '\uB0000' .. '\uBFFFD' | '\uC0000' .. '\uCFFFD' | '\uD0000' .. '\uDFFFD' | '\uE1000' .. '\uEFFFD'
  ;

PN_CHARS_U // ok
  : PN_CHARS_BASE | '_'
  ;

// adds ? and =
PN_CHARS
  : PN_CHARS_U | '-' | [0-9] | '\u00B7' | [\u0300-\u036F] | [\u203F-\u2040] | '?' | '='
  ;

PN_LOCAL_WITH_PLACEHOLDERS
  : PLACEHOLDER (PN_LOCAL_INNER_CHAR* PLACEHOLDER)*
  | (PN_LOCAL_FIRST_CHAR PN_LOCAL_INNER_CHAR*)? (PLACEHOLDER PN_LOCAL_INNER_CHAR*)+ PN_LOCAL_LAST_CHAR
  | PN_LOCAL_FIRST_CHAR (PN_LOCAL_INNER_CHAR* PLACEHOLDER)+ (PN_LOCAL_INNER_CHAR* PN_LOCAL_LAST_CHAR)?
  ;

// extends PN_LOCAL in the original grammar to allow  #, ; and /
// original (PN_CHARS_U | ':' | [0-9] | PLX) ((PN_CHARS | '.' | ':' | PLX)* (PN_CHARS | ':' | PLX))?
PN_LOCAL
  : PN_LOCAL_FIRST_CHAR (PN_LOCAL_INNER_CHAR* PN_LOCAL_LAST_CHAR)?
  ;

PLX // ok
  : PERCENT | PN_LOCAL_ESC
  ;

// %-encoded sequences are not decoded during processing
PERCENT // ok
  : '%' HEX HEX
  ;

HEX // ok
  : [0-9] | [A-F] | [a-f]
  ;

// reserved character escape sequences for local names only
PN_LOCAL_ESC  // ok
  : '\\' ('_' | '~' | '.' | '-' | '!' | '$' | '&' | '\'' | '(' | ')' | '*' | '+' | ',' | ';' | '=' | '/' | '?' | '#' | '@' | '%')
  ;

// adds #
fragment PN_LOCAL_FIRST_CHAR
  : PN_CHARS_U | ':' | [0-9] | PLX | '#'
  ;

// adds ;, # and /  (? and = through PN_CHARS)
fragment PN_LOCAL_INNER_CHAR
  : PN_CHARS | '.' | ':' | PLX | ';' | '#' | '/'
  ;

// adds /  (? and = through PN_CHARS)
fragment PN_LOCAL_LAST_CHAR
  : PN_CHARS | ':' | PLX | '/'
  ;

// adds ; (? and = through PN_CHARS)
fragment IRIREF_INNER_CHAR
  :  PN_CHARS | '.' | ':' | '/' | '\\' | '#' | '@' | '%' | '&' | UCHAR | ';'
  ;


