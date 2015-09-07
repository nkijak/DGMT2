package com.kinnack.dgmt2;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.kinnack.dgmt2.option.Function1;
import com.kinnack.dgmt2.option.Option;
import com.google.common.base.Joiner;

import static com.kinnack.dgmt2.option.Option.None;

public class QueryBuilder {
    private String[] projectionColumns;
    private String tableName;
    private Option<String> whereClause;
    private String[] whereParams;
    private Option<String> groupedBy;
    private Option<String> having;
    private Option<String> order;
    private boolean isDistinct = false;

    private QueryBuilder(String tableName, String[] columns, Option<String> whereClause, String[] whereParams, Option<String> groupBy, Option<String> having, Option<String> order, boolean isDistinct) {
        this.tableName = tableName;
        this.projectionColumns = columns;
        this.whereClause = whereClause;
        this.whereParams = whereParams;
        this.groupedBy = groupBy;
        this.having = having;
        this.order = order;
        this.isDistinct = isDistinct;
    }


    public static QueryBuilder select(String... projectionColumns) {
       return new QueryBuilder(null, projectionColumns, None(), null, None(), None(), None(), false);
    }

    public static QueryBuilder selectDistinct(String... projectionColumns) {
        return new QueryBuilder(null, projectionColumns, None(), null, None(), None(), None(), true);
    }

    public QueryBuilder from(String tableName) {
        return new QueryBuilder(tableName, this.projectionColumns, this.whereClause, this.whereParams, this.groupedBy, this.having, this.order, this.isDistinct);
    }

    public QueryBuilder where(String clause) {
        return new QueryBuilder(this.tableName, this.projectionColumns, Option.apply(clause), this.whereParams, this.groupedBy, this.having, this.order, this.isDistinct);
    }

    public QueryBuilder replacedBy(String... params) {
        return new QueryBuilder(this.tableName, this.projectionColumns, this.whereClause, params, this.groupedBy, this.having, this.order, this.isDistinct);
    }

    public QueryBuilder groupBy(String groupBy) {
        return new QueryBuilder(this.tableName, this.projectionColumns, this.whereClause, this.whereParams, Option.apply(groupBy), this.having, this.order, this.isDistinct);
    }

    public QueryBuilder having(String having) {
        return new QueryBuilder(this.tableName, this.projectionColumns, this.whereClause, this.whereParams, this.groupedBy, Option.apply(having), this.order, this.isDistinct);
    }

    public QueryBuilder orderBy(String order) {
        return new QueryBuilder(this.tableName, this.projectionColumns, this.whereClause, this.whereParams, this.groupedBy, this.having, Option.apply(order), this.isDistinct);
    }

    public static Cursor using(SQLiteDatabase db, QueryBuilder qb) {
        return   db.query(
                    qb.isDistinct,
                    qb.tableName,
                    qb.projectionColumns,
                    qb.whereClause.getOrElse(null),
                    qb.whereParams,
                    qb.groupedBy.getOrElse(null) /* groupby */,
                    qb.having.getOrElse(null) /* having */,
                    qb.order.getOrElse(null),
                    null /*limit*/);
    }

    public String toString() {
        final String base = "select " +(isDistinct ? "distinct ":"")+ Joiner.on(", ").join(this.projectionColumns)+" from "+this.tableName ;
        final String baseWhere = whereClause.map(new Function1<String, String>() {
            @Override
            public String apply(String where) {
                String atWhere = " where "+where;
                if (whereParams != null) {
                    atWhere += "^ ";
                    int i = 0;
                    while(atWhere.contains("?")) {
                        atWhere = atWhere.replace("?", whereParams[i]);
                    }
                }
                return atWhere;
            }
        }).fold(base, new Function1<String, String>() {
            @Override
            public String apply(String where) {
                return base + " " + where;
            }
        });
        return order.fold(baseWhere, new Function1<String, String>() {
            @Override
            public String apply(String orderedBy) {
                return baseWhere + " ordered by "+orderedBy;
            }
        });
    }

}
