package org.jep21s.messenger.core.service.repo.cassandra.config.liquibase

import liquibase.Scope
import liquibase.exception.DatabaseException
import liquibase.exception.LockException
import liquibase.exception.UnexpectedLiquibaseException
import liquibase.executor.Executor
import liquibase.executor.ExecutorService
import liquibase.ext.cassandra.database.CassandraDatabase
import liquibase.ext.cassandra.lockservice.LockServiceCassandra
import liquibase.statement.core.LockDatabaseChangeLogStatement
import liquibase.statement.core.RawSqlStatement
import liquibase.util.NetUtil

/**
 * Фикс бага библиотеки расширения liquibase для Cassandra DB 4.33.0 и старше
 */
class LiquibaseLockServiceCassandra: LockServiceCassandra() {

  override fun getPriority(): Int {
    return super.getPriority() + 1
  }

  override fun acquireLock(): Boolean {

    if (super.hasChangeLogLock) {
      return true
    }

    val executor =
      Scope.getCurrentScope().getSingleton(ExecutorService::class.java).getExecutor("jdbc", database)

    try {
      database.rollback()
      super.init()


      if (isLocked(executor)) {
        return false
      } else {
        executor.comment("Lock Database")
        val rowsUpdated = executor.update(LockDatabaseChangeLogStatement())
          .let {
            if (it != 0) return@let it
            /**
             * Необходима данная проверка, так как драйвер касандры не возвращает
             * результат при обновлении строк, поэтому нужен явный select
             */
            if (isLockedByCurrentInstance(executor)) 1 else 0
          }
        if (rowsUpdated == -1 && !isLockedByCurrentInstance(executor)) {
          // another node was faster
          return false
        }
        if (rowsUpdated > 1) {
          throw LockException("Did not update change log lock correctly")
        }
        if (rowsUpdated == 0) {
          // another node was faster
          return false
        }
        database.commit()
        Scope.getCurrentScope().getLog(this.javaClass).info("successfully.acquired.change.log.lock")


        hasChangeLogLock = true

        database.setCanCacheLiquibaseTableInfo(true)
        return true
      }
    } catch (e: Exception) {
      throw LockException(e)
    } finally {
      try {
        database.rollback()
      } catch (e: DatabaseException) {
      }
    }
  }

  @Throws(DatabaseException::class)
  private fun isLocked(executor: Executor): Boolean {
    // Check to see if current process holds the lock each time
    return isLockedByCurrentInstance(executor)
  }

  @Throws(DatabaseException::class)
  private fun isLockedByCurrentInstance(executor: Executor): Boolean {
    val lockedBy = NetUtil.getLocalHostName() + " (" + NetUtil.getLocalHostAddress() + ")"
    return executeCountQuery(
      executor,
      "SELECT COUNT(*) FROM " + getChangeLogLockTableName() + " WHERE " +
          "LOCKED = TRUE AND LOCKEDBY = '" + lockedBy + "' ALLOW FILTERING"
    ) > 0
  }

  private fun getChangeLogLockTableName(): String? {
    if (database.getLiquibaseCatalogName() != null) {
      return database.getLiquibaseCatalogName() + "." +
          database.getDatabaseChangeLogLockTableName()
    } else {
      return database.getDatabaseChangeLogLockTableName()
    }
  }

  @Throws(DatabaseException::class)
  private fun executeCountQuery(executor: Executor, query: String): Int {
    if (!query.contains("SELECT COUNT(*)")) {
      throw UnexpectedLiquibaseException("Invalid count query: " + query)
    }
    if (CassandraDatabase.isAwsKeyspacesCompatibilityModeEnabled()) {
      Scope.getCurrentScope().getLog(LockServiceCassandra::class.java)
        .fine("AWS Keyspaces compatibility mode enabled: using alternative count query")
      val altQuery = query.replace("(?i)SELECT COUNT\\(\\*\\)".toRegex(), "SELECT *")
      val rows = executor.queryForList(RawSqlStatement(altQuery))
      return rows.size
    } else {
      return executor.queryForInt(RawSqlStatement(query))
    }
  }
}