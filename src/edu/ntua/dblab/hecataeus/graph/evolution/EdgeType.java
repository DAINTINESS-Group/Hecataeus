/**
 * @author George Papastefanatos, National Technical University of Athens
 * @author Fotini Anagnostou, National Technical University of Athens
 */

package edu.ntua.dblab.hecataeus.graph.evolution;

/**
 * The class gathers the possible types of  edges
 *
 */
public enum EdgeType {
	EDGE_TYPE_SCHEMA ,
	EDGE_TYPE_CONDITION,
	EDGE_TYPE_MAPPING,
	EDGE_TYPE_WHERE ,
	EDGE_TYPE_FROM ,
	EDGE_TYPE_GROUP_BY ,
	EDGE_TYPE_ALIAS ,
	EDGE_TYPE_CALLING ,
	EDGE_TYPE_OPERATOR,
	EDGE_TYPE_HAVING ,
	EDGE_TYPE_ORDER_BY,
	EDGE_TYPE_CONTAINS
	;
	
	/**
	 * Converts from the enum representation of a type to the corresponding String representation
	 *
	 */
	public String ToString() {
		return name();
	}

	/**
	 * Converts from the String representation of a type to the corresponding enum representation
	 *
	 */
	public static EdgeType toEdgeType(String value) {
		return valueOf(value);
	}
}
