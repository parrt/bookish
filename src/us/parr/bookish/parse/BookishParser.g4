parser grammar BookishParser;

options {
	tokenVocab=BookishLexer;
}

document : element * ;

element : block_eqn | OTHER ;

block_eqn : BLOCK_EQN eqn BLOCK_EQN_END ;

eqn : eqn_element* ;

eqn_element : AMP | UNDERSCORE | EQN_OTHER ;
