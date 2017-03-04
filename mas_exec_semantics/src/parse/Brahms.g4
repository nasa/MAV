grammar Brahms;

compilationUnit
	:  
	   packageDeclaration?
	   importDeclaration*
	   typeDeclaration*
           EOF
	;

packageDeclaration 
	: 
	  'package' qualifiedName ';' 
	;

importDeclaration 
	: 
	  'import' qualifiedName ('.' '*')? ';'
	;

typeDeclaration
	:
		groupDeclaration
	| 	agentDeclaration
	|	classDeclaration
	|	objectDeclaration
	|	conceptClassDeclaration
	|	conceptObjectDeclaration
	|	areaDefDeclaration
	| 	areaDeclaration
	|	pathDeclaration
	;

groupDeclaration
	:
		'group' groupName groupMembership?
		 groupBodyDeclaration
	;

groupName
    :
        Identifier
    ;

agentDeclaration
	:
		('external')? 'agent' agentName groupMembership? 
		groupBodyDeclaration
	;

agentName
    :
        Identifier
    ;

classDeclaration
	:
		'class' className classInheritance?
		 groupBodyDeclaration
	;

className
    :
        Identifier
    ;

objectDeclaration
	:
		'object' objectName 'instanceof' className
		conceptualMembership? 
		groupBodyDeclaration 
	;

objectName
    :
        Identifier
    ;

conceptualMembership
	:
		'partof' lstOfConcepts
	;

groupMembership 
	:
		'memberof' lstOfConcepts
	;


groupBodyDeclaration
	:
		'{' 
		(displayDeclaration)?
		(costDeclaration)?
		(timeUnitDeclaration)?
        (locationDeclaration)?
		(iconDeclaration)?
		attributesList?
		relationsList?
		initialBeliefsList?
		initialFactsList?
		activitiesList?
		workFramesList?
		thoughtFramesList?
		'}'
	;

classInheritance
	:
		'extends' lstOfConcepts
	;


activitiesList
	:
		'activities'
		':'
		activityDeclaration*
	;

activityDeclaration
	:
        primitiveActivity
	|   communicateActivity
    |   broadcastActivity
	|   javaActivity
	;

primitiveActivity
	:
		'primitive_activity'
		 activityName
		 '(' paramDeclaration? ')'	
		 '{' activitySetup
		 '}'
	;

communicateActivity
	:
		'communicate'
        activityName
		 '(' paramDeclaration? ')'	
		 '{' activitySetup
		     'with' ':' lstOfConcepts ';'
		     'about' ':' transferList ';'
		     whenDeclaration?
		 '}'
	;

broadcastActivity
    :
        'broadcast'
        activityName
        '(' paramDeclaration? ')'	
		 '{' activitySetup
		     'about' ':' transferList ';'
		     whenDeclaration?
		 '}'
    ;

javaActivity
	:
		'java'
		Identifier
		 '(' paramDeclaration? ')'	
		 '{' activitySetup
		     'class' ':' StringLiteral ';'
		     whenDeclaration?
		 '}'
		
	;

lstOfConcepts
	:
		Identifier 
		(',' Identifier)*
	;


transferList
	:
		transferDefinition
		(',' transferDefinition)*
	;

whenDeclaration
	:
		'when' ':'
		timeOfWhen ';'
	;

timeOfWhen
	:
		'start'
		| 'end'
		| Identifier
	;

transferDefinition
	:
		 transferType '(' transferAction ')'
	;

transferType
	:
		'send'
		| 'receive'
	;

transferAction
	:
		Identifier
		| resultComparison
	;

activitySetup
	:	
		 displayDeclaration?
		 priorityDeclaration?
		 randomDeclaration?
		 minDurationDeclaration?
		 maxDurationDeclaration?
	;

paramDeclaration
	:
		param ( ',' param)*
	;

param	
	:
		typeDef Identifier
	;

priorityDeclaration
	:
		'priority' ':'	priorityOrDurationValue ';'
	;

maxDurationDeclaration
	:
		'max_duration' ':' priorityOrDurationValue ';'
	;

minDurationDeclaration	
	:
		'min_duration' ':' priorityOrDurationValue ';'
	;

randomDeclaration
	:
		'random' ':' randomValue ';'
	;

priorityOrDurationValue
	:
		Number
		| Identifier
	;

randomValue
	:
		BooleanLiteral
		| Identifier
	;

workFramesList
	:
		'workframes'
		':'
		workFrameDeclaration*
	;

thoughtFramesList
	:
		'thoughtframes'
		':'
		thoughtFrameDeclaration*
	;

//workframe 	::= 	workframe workframe-name
//			{
//			{ display : ID.literal-string ; }
//			{ type : factframe | dataframe ; }
//			{ repeat : ID.truth-value ; }
//			{ priority : ID.unsigned ; }
//			{ variable-decl }
//			{ detectable-decl }
//			{ [ precondition-decl workframe-body-decl ] | workframe-body-decl }
//			} 

workFrameDeclaration
	:
		'workframe' Identifier
		'{'
			(displayDeclaration)?
			(frameTypeDeclaration)?
			(repeatDeclaration)?
			(priorityDeclaration)?
			(variableDeclaration)?
			(preconditionDeclaration)
			(workFrameBodyDeclaration)
		'}'	
	;


//thoughtframe 	::= 	thoughtframe thoughtframe-name
//			{
//			{ display : ID.literal-string ; }
//			{ repeat : ID.truth-value ; }
//			{ priority : ID.unsigned ; }
//			{ WFR.variable-decl }
//			{ [ WFR.precondition-decl thoughtframe-body-decl ] | thoughtframe-body-decl }
//			}

thoughtFrameDeclaration
	:
		'thoughtframe' Identifier 
		'{'
			(displayDeclaration)?
			(repeatDeclaration)?
			(priorityDeclaration)?
			(variableDeclaration)?
			(preconditionDeclaration)
			(thoughtFrameBodyDeclaration)
		'}'
	;

//thoughtframe-name 	::= 	ID.name
//thoughtframe-body-decl 	::= 	do ( { [ thoughtframe-body-element ]* } )
//thoughtframe-body-element 	::= 	CON.consequence

frameTypeDeclaration
	:
	'type' ':'	
	frameType
	';'
	;

frameType 
	:
		'factframe'
		| 'dataframe'
	;



variableDeclaration
	:
		'variables' 
		':'
		variable*
	;

detectableDeclaration
	:
		'detectables'
		':'
	;

//precondition-decl 	::= 	when ( { [ PRE.precondition ][ and PRE.precondition ]* } )

preconditionDeclaration
	:
		'when' '('
		       (preconditionList)?
		')'
	;

preconditionList
	:
		preCondition
		('and' preCondition)*
	;

//workframe-body-decl 	::= 	do ( { [ workframe-body-element ]* } )

workFrameBodyDeclaration
	:
		'do' '{'
		     workFrameBodyElement*
		'}'	
	;

//workframe-body-element 	::= 	[ PAC.activity-ref | CON.consequence | DEL.delete-operation ]

workFrameBodyElement
	:
		consequence
		| activityRef
	;


thoughtFrameBodyDeclaration
	:
		'do' '{'
		     thoughtFrameBodyElement*
		 '}'
	;

thoughtFrameBodyElement
	:
		consequence
	;

//activity-ref 	::= 	activity-name ( { param-expr [ , param-expr ]* } ) ;

activityRef
	:
		activityName
		'('  value
		(',' value)* 
		')'		
		';'
	;

activityName 
    :
        Identifier
    ;

initialBeliefsList
	:
		'initial_beliefs'
		':'
		initialBeliefOrFactDeclaration*
	;



initialFactsList
	:
		'initial_facts'
		':'
		initialBeliefOrFactDeclaration*
	;

//initial-belief 	::= 	( [ value-expression | relational-expression] )


initialBeliefOrFactDeclaration
	:
	    '(' 
        ( valueExpression 
        | relationalExpression )
        ')' ';'
	;

//value-expression 	::= 	obj-attr equality-operator value |
//				obj-attr equality-operator sgl-object-ref


valueExpression
	:
        objAttr equalityOperator sglObjectRef 
	|	objAttr equalityOperator value 
	;


//relational-expression 	::= 	tuple-object-ref REL.relation-name sgl-object-ref 
//					{ is ID.truth-value }

relationalExpression
	:
		tupleObjectRef relationName sglObjectRef
		isTruthVal?
	;



//variable 	::= 	{ collectall | foreach | forone }
//			( ATT.type-def )
//			variable-name
//			{ ATT.variable-body }
//			;

variable
	:
		('collectall' | 'foreach' | 'forone')
		'(' typeDef ')'
		variableName
		';'
	;

//variable-name 	::= 	ID.name

variableName
	: 
		Identifier
	;

//variable-body 	::= 	{
//			{ display : ID.literal-string }
//			}

//precondition 	::= 	{ [ known | unknown ] } ( novalcomparison ) |
//			{ [ knownval | not ] } ( evalcomparison )


preCondition 
	:
		('known' | 'unknown') '(' noValComparison ')'
		| ('knownval' | 'not') '(' evalComparison ')'
	;

//novalcomparison 	::= 	BEL.obj-attr |
//				BEL.obj-attr REL.relation-name |
//				BEL.tuple-object-ref REL.relation-name

noValComparison 
	:
		objAttr
		| objAttr relationName
		| tupleObjectRef relationName
	;

//evalcomparison 	::= 	eval-val-comp | rel-comp

evalComparison
	:
		evalValComp
		| relComp isTruthVal?
	;


resultComparison 
	:
		detectValComp
		| detectRelComp isTruthVal?
	;


//detect-val-comp 	::= 	obj-attr |
//				obj-attr BEL.evaluation-operator PRE.expression |
//				obj-attr BEL.evaluation-operator obj-attr |
//				obj-attr BEL.equality-operator ID.literal-symbol |
//				obj-attr BEL.equality-operator ID.literal-string |
//				obj-attr BEL.equality-operator sgl-object-ref

//TODO order matters.... start with expression (boils to a number),
//boolean, string, identifier, object without unknown, object with unknown
detectValComp
	:
		objAttr
		| objAttr evaluationOperator expression
		| objAttr evaluationOperator objAttr
        | objAttr equalityOperator BooleanLiteral 
		| objAttr equalityOperator Identifier
		| objAttr equalityOperator StringLiteral
		| objAttr equalityOperator sglObjectRef
	;

//detect-rel-comp 	::= 	detectable-object REL.relation-name |
//				detectable-object REL.relation-name sgl-object-ref
//				{ is ID.truth-value } 

detectRelComp
	:
		tupleObjectRef relationName
		| tupleObjectRef relationName sglObjectRef
	;

//eval-val-comp 	::= 	expression BEL.evaluation-operator expression |
//				BEL.obj-attr BEL.equality-operator ID.literal-symbol |
//				BEL.obj-attr BEL.equality-operator ID.literal-string |
//				BEL.obj-attr BEL.equality-operator BEL.sgl-object-ref |
//				BEL.sgl-object-ref BEL.equality-operator BEL.sgl-object-ref


evalValComp
	:
		expression evaluationOperator expression
        | objAttr equalityOperator BooleanLiteral 
		| objAttr equalityOperator Identifier
		| objAttr equalityOperator StringLiteral
		| objAttr equalityOperator sglObjectRef
		| sglObjectRef equalityOperator sglObjectRef
	;


//consequence 	::= 	conclude ( ( resultcomparison )
//			{ , fact-certainty }
//			{ , belief-certainty } ) ;

consequence
	:
		'conclude' '('
			   '('consequenceResult')'
			   (',' factCertainty)?
			   (',' beliefCertainty)?
			    ')' ';'
	;


//resultcomparison 	::= 	result-val-comp | PRE.rel-comp

consequenceResult 
	:
		resultValComp
		| relComp
	;

//result-val-comp 	::= 	BEL.obj-attr BEL.equality-operator PRE.expression |
//				BEL.obj-attr BEL.equality-operator ID.literal-symbol |
//				BEL.obj-attr BEL.equality-operator ID.literal-string |
//				BEL.obj-attr BEL.equality-operator BEL.sgl-object-ref |
//				BEL.tuple-object-ref BEL.equality-operator BEL.sgl-object-ref


resultValComp
	:	
		objAttr equalityOperator expression
    | objAttr equalityOperator BooleanLiteral
		| objAttr equalityOperator Identifier
		| objAttr equalityOperator StringLiteral
		| objAttr equalityOperator sglObjectRef
		| tupleObjectRef equalityOperator sglObjectRef
		
	;

//fact-certainty 	::= 	fc : ID.unsigned

factCertainty 
	:
		'fc' ':' Number
	;
	      

//belief-certainty 	::= 	bc : ID.unsigned

beliefCertainty
	:
		'bc' ':' Number
	;

relComp
	:
		  objAttr relationName objAttr
		| objAttr relationName sglObjectRef
		| tupleObjectRef relationName sglObjectRef
	
	;



expression
	:
		expression ('+'|'-') expression
		| term 
	;

term
	:
		term ('*' | '/' | 'div' | 'mod') term
		| factor
	;

factor
	:
		primary
		| factor '^' primary
	;

primary
	:
		element
		| '-' primary
	;

element
	:
		Number
		| objAttr
		| Identifier
		| 'unknown'
	;

// equality-operator 	::= 	= | !=

equalityOperator 
	:
		'='
		| '!='
	;

//evaluation-operator 	::= 	equality-operator | > | >= | < | <=

evaluationOperator
	:
		'>'
		| '>='
		| '<'
		| '<='
		| equalityOperator
	;


//collection-index 	::= 	ID.literal-string |
//				ID.unsigned |
//				VAR.variable-name |
//				PAC.param-name


collectionIndex
	:
		StringLiteral
		| Number
		| Identifier
	;


//obj-attr 	::= 	tuple-object-ref . ATT.-name
//			{ ( collection-index ) }

objAttr 
	:
		tupleObjectRef '.' attributeName
		('(' collectionIndex ')')?
	;

//tuple-object-ref 	::= 	AGT.agent-name |
//				OBJ.object-name |
//				COB.conceptual-object-name |
//				ARE.area-name |
//				VAR.variable-name |
//				PAC.param-name |
//				current

tupleObjectRef
	:
		Identifier
		| 'current'
	;


//sgl-object-ref 	::= 	AGT.agent-name |
//			OBJ.object-name |
//			COB.conceptual-object-name |
//			ARE.area-name |
//			VAR.variable-name |
//			PAC.param-name |
//			unknown
//			current

sglObjectRef
	:
		tupleObjectRef
		| 'unknown'
	;


//value 	::= 	ID.literal-string | ID.number | PAC.param-name | unknown

value 
    :
        BooleanLiteral
    |   StringLiteral 
	|   Number
	|   Identifier
	|   'unknown'
    ;




conceptClassDeclaration
	:
		'conceptual class foo'
	;
conceptObjectDeclaration
	:
		'conceptual object foo'
	;
areaDefDeclaration
	:
		'areadef foo'
	;
areaDeclaration
	:
		'area foo'
	;
pathDeclaration
	:
		'path foo'
	;

displayDeclaration
	:
		 'display' ':' StringLiteral ';' 
	;

costDeclaration
	:
		'cost' ':' Number ';'
	;

timeUnitDeclaration
	:
		'time_unit' ':' Number ';'
	;

locationDeclaration
    :
        'location' ':' areaName ';'
    ;

areaName
    :
        Identifier
    ;

iconDeclaration
	:
		'icon' ':' StringLiteral ';'
	;

attributesList
	:
		'attributes' ':'
		attributeDeclaration*
	;

attributeDeclaration
	:
		visibilityType?	
		attributeType
		attributeName
		';'
	;


visibilityType 
	:
		'private'
    |   'protected'
	|   'public'
	;

attributeType
    :
         'java' '(' javaTypeDef ')'
    |    'relation' '(' typeDef ')'
    |    typeDef
    ;

javaTypeDef
    :
        'todo'
    ;

typeDef 
	:
		primitiveTypes
		| collectionTypes
		| Identifier
	;

attributeName
	:
		Identifier
	;


qualifiedName
	:
		Identifier ('.' Identifier)*
	;

relationsList
	:
		'relations' 
		':'
		relationDeclaration*
	;

relationDeclaration
	:
		visibilityType?
		relationTypeDef
		relationName
		';'
	;

relationTypeDef 
	:
		Identifier
	;

relationName
	:
		Identifier
	;

isTruthVal 
    :
    	'is' BooleanLiteral
    ;


// Boolean Literals
// truth-value 	::= 	true | false | unknown

BooleanLiteral
    :   'true'
    |   'false'
    | 	'unknown'
    ;

//Identifiers (must appear after all keywords in the grammar)
// https://github.com/antlr/grammars-v4/blob/master/java/Java.g4

Identifier
	:  
		JavaLetter JavaLetterOrDigit*
     	;


//value-type-def 	::= 	[ int | long | double | symbol | string | boolean ]
//collection-type-def 	::= 	[ map ]

primitiveTypes
	:
		'int'
		| 'long'
		| 'double'
		| 'symbol'
		| 'string'
		| 'boolean'
	;

collectionTypes
	:
		'map'
	;

repeatDeclaration
	:
	'repeat' ':'
		 BooleanLiteral
	';'
	;

//number 	::= 	[ integer | long | double ]
//integer 	::= 	{ + | - } unsigned
//long 	::= 	{ + | - } unsigned { l | L }
//unsigned 	::= 	[ digit ]+
//double 	::= 	[ integer.unsigned ]


Number 
       :
		SignedInteger (IntegerTypeSuffix)?
		| SignedInteger ('.' Number)?
       ;


// §3.10.1 Integer Literals

IntegerLiteral
    :   DecimalIntegerLiteral
    |   HexIntegerLiteral
    |   OctalIntegerLiteral
    |   BinaryIntegerLiteral
    ;

fragment
DecimalIntegerLiteral
    :   DecimalNumeral IntegerTypeSuffix?
    ;

fragment
HexIntegerLiteral
    :   HexNumeral IntegerTypeSuffix?
    ;

fragment
OctalIntegerLiteral
    :   OctalNumeral IntegerTypeSuffix?
    ;

fragment
BinaryIntegerLiteral
    :   BinaryNumeral IntegerTypeSuffix?
    ;

fragment
IntegerTypeSuffix
    :   [lL]
    ;

fragment
DecimalNumeral
    :   '0'
    |   NonZeroDigit (Digits? | Underscores Digits)
    ;

fragment
Digits
    :   Digit (DigitOrUnderscore* Digit)?
    ;

fragment
Digit
    :   '0'
    |   NonZeroDigit
    ;

fragment
NonZeroDigit
    :   [1-9]
    ;

fragment
DigitOrUnderscore
    :   Digit
    |   '_'
    ;

fragment
Underscores
    :   '_'+
    ;

fragment
HexNumeral
    :   '0' [xX] HexDigits
    ;

fragment
HexDigits
    :   HexDigit (HexDigitOrUnderscore* HexDigit)?
    ;

fragment
HexDigit
    :   [0-9a-fA-F]
    ;

fragment
HexDigitOrUnderscore
    :   HexDigit
    |   '_'
    ;

fragment
OctalNumeral
    :   '0' Underscores? OctalDigits
    ;

fragment
OctalDigits
    :   OctalDigit (OctalDigitOrUnderscore* OctalDigit)?
    ;

fragment
OctalDigit
    :   [0-7]
    ;

fragment
OctalDigitOrUnderscore
    :   OctalDigit
    |   '_'
    ;

fragment
BinaryNumeral
    :   '0' [bB] BinaryDigits
    ;

fragment
BinaryDigits
    :   BinaryDigit (BinaryDigitOrUnderscore* BinaryDigit)?
    ;

fragment
BinaryDigit
    :   [01]
    ;

fragment
BinaryDigitOrUnderscore
    :   BinaryDigit
    |   '_'
    ;

// §3.10.2 Floating-Point Literals

FloatingPointLiteral
    :   DecimalFloatingPointLiteral
    |   HexadecimalFloatingPointLiteral
    ;

fragment
DecimalFloatingPointLiteral
    :   Digits '.' Digits? ExponentPart? FloatTypeSuffix?
    |   '.' Digits ExponentPart? FloatTypeSuffix?
    |   Digits ExponentPart FloatTypeSuffix?
    |   Digits FloatTypeSuffix
    ;

fragment
ExponentPart
    :   ExponentIndicator SignedInteger
    ;

fragment
ExponentIndicator
    :   [eE]
    ;

fragment
SignedInteger
    :   Sign? Digits
    ;

fragment
Sign
    :   [+-]
    ;

fragment
FloatTypeSuffix
    :   [fFdD]
    ;

fragment
HexadecimalFloatingPointLiteral
    :   HexSignificand BinaryExponent FloatTypeSuffix?
    ;

fragment
HexSignificand
    :   HexNumeral '.'?
    |   '0' [xX] HexDigits? '.' HexDigits
    ;

fragment
BinaryExponent
    :   BinaryExponentIndicator SignedInteger
    ;

fragment
BinaryExponentIndicator
    :   [pP]
    ;



// §3.10.4 Character Literals

CharacterLiteral
    :   '\'' SingleCharacter '\''
    |   '\'' EscapeSequence '\''
    ;

fragment
SingleCharacter
    :   ~['\\]
    ;
// §3.10.5 String Literals
StringLiteral
    :   '"' StringCharacters? '"'
    ;
fragment
StringCharacters
    :   StringCharacter+
    ;
fragment
StringCharacter
    :   ~["\\]
    |   EscapeSequence
    ;
// §3.10.6 Escape Sequences for Character and String Literals
fragment
EscapeSequence
    :   '\\' [btnfr"'\\]
    |   OctalEscape
    |   UnicodeEscape
    ;

fragment
OctalEscape
    :   '\\' OctalDigit
    |   '\\' OctalDigit OctalDigit
    |   '\\' ZeroToThree OctalDigit OctalDigit
    ;

fragment
UnicodeEscape
    :   '\\' 'u' HexDigit HexDigit HexDigit HexDigit
    ;

fragment
ZeroToThree
    :   [0-3]
    ;

// §3.10.7 The Null Literal

NullLiteral
    :   'null'
    ;


fragment
JavaLetter
    :   [a-zA-Z$_] // these are the "java letters" below 0x7F
    |   // covers all characters above 0x7F which are not a surrogate
        ~[\u0000-\u007F\uD800-\uDBFF]
        {Character.isJavaIdentifierStart(_input.LA(-1))}?
    |   // covers UTF-16 surrogate pairs encodings for U+10000 to U+10FFFF
        [\uD800-\uDBFF] [\uDC00-\uDFFF]
        {Character.isJavaIdentifierStart(Character.toCodePoint((char)_input.LA(-2), (char)_input.LA(-1)))}?
    ;

fragment
JavaLetterOrDigit
    :   [a-zA-Z0-9$_] // these are the "java letters or digits" below 0x7F
    |   // covers all characters above 0x7F which are not a surrogate
        ~[\u0000-\u007F\uD800-\uDBFF]
        {Character.isJavaIdentifierPart(_input.LA(-1))}?
    |   // covers UTF-16 surrogate pairs encodings for U+10000 to U+10FFFF
        [\uD800-\uDBFF] [\uDC00-\uDFFF]
        {Character.isJavaIdentifierPart(Character.toCodePoint((char)_input.LA(-2), (char)_input.LA(-1)))}?
;


//
// Whitespace and comments
//

WS  
	:  
	   [ \t\r\n\u000C]+ -> skip
    	;

COMMENT
    :   '/*' .*? '*/' -> skip
    ;

LINE_COMMENT
    :   '//' ~[\r\n]* -> skip
    ;
