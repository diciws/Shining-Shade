package at.dici.shade.core.mysql;

import at.dici.shade.core.mysql.DBHandler.DataSource;
import at.dici.shade.utils.log.LogLevel;
import at.dici.shade.utils.log.Logger;
import com.google.common.base.Function;
import com.google.common.util.concurrent.*;
import lombok.Getter;

import java.beans.ConstructorProperties;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
/**
 * @author dici 01.05.2018
 */
@Getter
public class MySqlClass {
    @Getter
    private static MySqlClass instance;

    private final ListeningExecutorService THREAD_POOL = MoreExecutors
            .listeningDecorator( Executors.newCachedThreadPool() );
    private Connection connection;

    public MySqlClass( ) {
        MySqlClass.instance = this;
    }

    public void connect( ) {
        try {
            if(this.connection != null){
                return;
            }
            // Connect to database.
            this.connection = DataSource.getConnection();

            Logger.log(LogLevel.BOT, "MySQL Verbindung steht! -> " + this.connection);
        } catch ( SQLException e ) {
            Logger.log(LogLevel.FATAL, "MySQL -> no connection to DB possible");
            e.printStackTrace();
        }
    }

    public void isConnected( ){
        //Logger.log(LogLevel.WARNING, "Trying to resolve if MySQL Connection is still possible");
        if(this.connection != null){
            //Logger.log(LogLevel.INFORMATION, "Connection is still stable! Connection: " + this.connection);
        }else{
            Logger.log(LogLevel.FATAL, "Connection is broken and nobody knows why o.0");
            connect();
        }
    }

    public void disconnect( ) {
        try {
            Logger.log(LogLevel.WARNING, "MySQL disconnected successfully!");
            this.getConnection().close();
        } catch ( SQLException e ) {
            Logger.log(LogLevel.ERROR, "MySQL disconnect error");
            e.printStackTrace();
        }
    }

    /**
     * --CONSUMER--
     **/

    public <T> void queryAsync( final String sql, Function<ResultSet, T> function, Consumer<T> consumer,
                                final Object... parameters ) {
        Futures.addCallback( Futures.transform( this.queryAsync( sql, parameters ), resultSetRunnableTuple ->
                                Tuple.of( function.apply( resultSetRunnableTuple.getOne() ), resultSetRunnableTuple.getTwo() ),
                        this.getTHREAD_POOL() ),
                new FutureCallback<Tuple<T, Runnable>>() {
                    @Override
                    public void onSuccess( Tuple<T, Runnable> result ) {
                        consumer.accept( result.getOne() );
                        result.getTwo().run();
                    }

                    @Override
                    public void onFailure( Throwable t ) {
                        consumer.accept( null );
                    }
                }, this.getTHREAD_POOL() );
    }

    public void updateAsync( final String sql, Consumer<Boolean> consumer, final Object... parameters ) {
        Futures.addCallback( this.updateAsync( sql, parameters ), new FutureCallback<Boolean>() {
            @Override
            public void onSuccess( Boolean result ) {
                consumer.accept( result );
            }

            @Override
            public void onFailure( Throwable t ) {
                consumer.accept( null );
            }
        }, this.getTHREAD_POOL() );
    }

    /**
     * --LISTENABLE FUTURE--
     **/

    private ListenableFuture<Tuple<ResultSet, Runnable>> queryAsync( final String sql, final Object... parameters ) {
        return this.getTHREAD_POOL().submit( ( ) -> query( sql, parameters ) );
    }

    private ListenableFuture<Boolean> updateAsync( final String sql, final Object... parameters ) {
        return this.getTHREAD_POOL().submit( ( ) -> update( sql, parameters ) );
    }


    /**
     * --SYNC--
     **/

    private Tuple<ResultSet, Runnable> query( String sql, Object... parameters ) {
        AtomicReference<PreparedStatement> preparedStatement = new AtomicReference<>( null );
        AtomicReference<ResultSet> resultSet = new AtomicReference<>( null );

        isConnected( );
        try {
            Connection con = DataSource.getConnection();
            preparedStatement.set( con.prepareStatement( sql ) );

            int count = 1;
            Object[] var6 = parameters;
            int var7 = parameters.length;

            for(int var8 = 0; var8 < var7; ++var8) {
                Object parameter = var6[var8];
                preparedStatement.get().setObject(count, parameter);
                ++count;
            }

            resultSet.set( preparedStatement.get().executeQuery() );

            return Tuple.of( resultSet.get(), ( ) -> {
                try {
                    resultSet.get().close();
                    preparedStatement.get().close();
                    con.close();
                    Logger.log(LogLevel.INFORMATION, "MySQL query parameters successfully SQL: "+ sql);
                } catch ( SQLException e ) {
                    Logger.log(LogLevel.ERROR, "MySQL Statement query resultset/parameter! SQL: "+ sql);
                    e.printStackTrace();
                }
            } );
        } catch ( SQLException e ) {
            Logger.log(LogLevel.ERROR, "MySQL query in MySqlClass! SQL: "+ sql);
            e.printStackTrace();
        }
        return Tuple.of( null, ( ) -> {
        } );
    }

    private boolean update( String sql, Object... parameters ) {
        isConnected( );

        PreparedStatement preparedStatement = null;
        try {
            Connection con = DataSource.getConnection();
            preparedStatement = con.prepareStatement( sql );

            int count = 1;
            Object[] var5 = parameters;
            int var6 = parameters.length;

            for(int var7 = 0; var7 < var6; ++var7) {
                Object parameter = var5[var7];
                preparedStatement.setObject(count, parameter);
                ++count;
            }

            preparedStatement.executeUpdate();
            //preparedStatement.close();
            con.close();

            Logger.log(LogLevel.INFORMATION, "MySQL update parameters successfully: "+ sql);

            return true;
        } catch ( SQLException e ) {
            Logger.log(LogLevel.ERROR, "MySQL update parameters -> SQLException: "+ sql);
            e.printStackTrace();
        } finally {
            try {
                assert preparedStatement != null;
                preparedStatement.close();
            } catch ( SQLException e ) {
                Logger.log(LogLevel.ERROR, "MySQL update parameters -> close statement exception");
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean codeExits(String code) {
        try {
            Connection con = DataSource.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM steam_verify WHERE verify_code=?");
            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();

            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getcode(String tsid) {
        String code = "";
        try {
            Connection con = DataSource.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM steam_verify WHERE discordid=?");
            ps.setString(1, tsid);
            ResultSet rs = ps.executeQuery();
            code = rs.getString("tscode");
            ps.close();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return code;
    }


    private static final class Tuple<O, T> {
        private O one;
        private T two;

        static <O, T> Tuple<O, T> of(O one, T two) {
            return new Tuple<>(one, two);
        }

        @ConstructorProperties({"one", "two"})
        public Tuple(O one, T two) {
            this.one = one;
            this.two = two;
        }

        public O getOne() {
            return this.one;
        }

        public T getTwo() {
            return this.two;
        }

        public void setOne(O one) {
            this.one = one;
        }

        public void setTwo(T two) {
            this.two = two;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            } else if (!(o instanceof Tuple)) {
                return false;
            } else {
                Tuple<?, ?> other = (Tuple) o;
                Object this$one = this.getOne();
                Object other$one = other.getOne();
                if (this$one == null) {
                    if (other$one != null) {
                        return false;
                    }
                } else if (!this$one.equals(other$one)) {
                    return false;
                }

                Object this$two = this.getTwo();
                Object other$two = other.getTwo();
                if (this$two == null) {
                    if (other$two != null) {
                        return false;
                    }
                } else if (!this$two.equals(other$two)) {
                    return false;
                }

                return true;
            }
        }

        public int hashCode() {
            boolean PRIME = true;
            int result = 1;
            Object $one = this.getOne();
            result = result * 59 + ($one == null ? 43 : $one.hashCode());
            Object $two = this.getTwo();
            result = result * 59 + ($two == null ? 43 : $two.hashCode());
            return result;
        }

        public String toString() {
            return "MySqlClass.Tuple(one=" + this.getOne() + ", two=" + this.getTwo() + ")";
        }
    }


}
