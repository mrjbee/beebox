package org.monroe.team.android.box.db;

import org.monroe.team.android.box.Closure;
import org.monroe.team.android.box.Lists;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class Schema {

    public final int version;
    public final String name;
    private final HashMap<Class,Table> tableList;
    private final List<UpgradeDiff> upgradeDiffList = new ArrayList<>();

    public Schema(int version, String name, Class<? extends Table>... tables){
        this.version = version;
        this.name = name;
        tableList = new HashMap(tables.length);
        for (Class<? extends Table> tableClass : tables) {
            try {
                Table table = tableClass.newInstance();
                tableList.put(tableClass, table);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        registerUpgradeDiff();
    }

    protected void registerUpgradeDiff(){}

    final protected void upgradeToVersion(int version, String... scripts){
        if (scripts ==null || scripts.length == 0) return;
        upgradeDiffList.add(new UpgradeDiff(version, scripts));
    }

    final public void withEachTable(Closure<Table,Void> action){
        Lists.each(tableList.values(), action);
    }

    public <Table extends Schema.Table> Table table(Class<Table> tableClass) {
        return (Table) tableList.get(tableClass);
    }

    public static interface Table {
        public String createScript();
        void alterColumns(int version, Closure<String, Void> sqlAction);
    }

    public static abstract class VersionTable implements Table {

        private String tableName;
        private final List<VersionTableColumn> tableColumns = new ArrayList<>();

        final public TableBuilder define(int version, String tableName){
            this.tableName = tableName;
            return new TableBuilder(version,tableColumns);
        }

        final public TableBuilder define(int version){
            return new TableBuilder(version,tableColumns);
        }

        @Override
        final public String createScript() {

            if (tableColumns.isEmpty()) throw new IllegalStateException("Why do you need empty table?");

            StringBuilder builder = new StringBuilder("CREATE TABLE IF NOT EXISTS " + tableName + " (");
            for (VersionTableColumn tableColumn : tableColumns) {
                builder.append(tableColumn.fieldName +" "+tableColumn.fieldDefinition+" ,");
            }
            builder.deleteCharAt(builder.length()-1);
            builder.append(")");
            return builder.toString();
        }

        @Override
        final public void alterColumns(final int version, final Closure<String, Void> sqlAction) {
            Lists.each(tableColumns, new Closure<VersionTableColumn, Void>() {
                @Override
                public Void execute(VersionTableColumn tableColumn) {
                    if (tableColumn.version == version){
                        sqlAction.execute("ALTER TABLE "+tableName+" ADD COLUMN "
                                +tableColumn.fieldName+" "+tableColumn.fieldDefinition);
                    }
                    return null;
                }
            });
        }

        protected final class VersionTableColumn {

            private final int version;
            private final String fieldName;
            private final String fieldDefinition;

            public VersionTableColumn(int version, String fieldName, String fieldDefinition) {
                this.version = version;
                this.fieldName = fieldName;
                this.fieldDefinition = fieldDefinition;
            }
        }

        protected final class TableBuilder {
            private final int version;
            private final List<VersionTableColumn> columnLists;

            public TableBuilder(int version, List<VersionTableColumn> columnLists) {
                this.version = version;
                this.columnLists = columnLists;
            }

            final public TableBuilder column(String name, String definition){
                tableColumns.add(new VersionTableColumn(version, name, definition));
                return this;
            }
        }
    }

    private static class UpgradeDiff{

        private final int ver;
        private final String[] scripts;

        private UpgradeDiff(int ver, String[] scripts) {
            this.ver = ver;
            this.scripts = scripts;
        }
    }


}
