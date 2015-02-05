package dao.query;

/**
 * Created by Ali on 09/12/14.
 */
public class QueryStubBuilder {

    private String selectStatement = null;
    private String fromStatement = null;
    private String whereStatement = null;
    private String leftJoinStatment = null;
    private String sortStatment = null;


    /**
     * This method is to set the fields we want to get in the
     * Query in our case is the movie id (mid)
     *
     * @param field
     */
    public void addSelectField(String field) {
        if (selectStatement == null) {
            selectStatement = "SELECT " + field + " ";
        } else {
            selectStatement += ", " + field + " ";
        }
    }

    /**
     * This method is to set the Table in the Query
     *
     * @param table
     */
    public void addFromTable(String table) {
        if (fromStatement == null) {
            fromStatement = "FROM " + table + " ";
        } else {
            fromStatement += ", " + table + " ";
        }
    }

    public void addLeftJoinTable(String table) {
        if (leftJoinStatment == null) {
            leftJoinStatment = "left outer Join " + table + " ";
        } else {
            leftJoinStatment += "ON " + table + " ";
        }
    }

    public void addSortTable() {
        if (fromStatement == null) {
            fromStatement = "Group BY m.mid  ASC ";
        }
    }



    /**
     * This method to set either where clause or AND
     * depend on the Condition after first call for
     * the Method wereStatment is not any more null
     * for the second call it will be adding AND to the
     * String
     *
     * @param condition
     */
    public void addWhereAndCondition(String condition) {
        if (whereStatement == null) {
            whereStatement = "WHERE " + condition + " ";
        } else {
            whereStatement += "AND " + condition + " ";
        }
    }

    /**
     * This method check if one of the Strings is null
     * if not build a Query
     *
     * @throws java.lang.IllegalStateException
     * @return the built Query
     */
    public String getQuery() {
        if (selectStatement == null ||
            fromStatement == null ||
            whereStatement == null) {
            throw new IllegalStateException("query cannot be built");
        }

        if (leftJoinStatment != null) {
            return selectStatement + fromStatement + leftJoinStatment + whereStatement;
        } else {
            return selectStatement + fromStatement + whereStatement + sortStatment;
        }
    }

}
