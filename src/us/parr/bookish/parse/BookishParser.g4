parser grammar BookishParser;

options {
	tokenVocab=BookishLexer;
}

document : element * ;

element : block_eqn | eqn | OTHER ;

block_eqn : BLOCK_EQN block_eqn_elements END_BLOCK_EQN ;

block_eqn_elements : block_eqn_element* ;

block_eqn_element
	:	BLOCK_EQN_AMP
	|	BLOCK_EQN_UNDERSCORE
	|	BLOCK_EQN_END_ROW
	|	BLOCK_EQN_OTHER
	;

eqn : EQN eqn_elements END_EQN ;

eqn_elements : eqn_element* ;

eqn_element
	:	EQN_UNDERSCORE
	|	EQN_NL
	|	EQN_OTHER
	;

