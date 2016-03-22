/*
 * Copyright 2013 Maurício Linhares
 *
 * Maurício Linhares licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.github.mauricio.async.db.pool

import com.github.mauricio.async.db.Connection

import scala.concurrent.{ExecutionContext, Future, Promise}

/**
 * This provides a base interface describing a non-specific pooled connection, allowing those who don't care which
 * particular connection type they're using to write more common code (as Connection itself does).
 */

trait PooledConnection extends Connection {

  /**
   *
   * Returns a connection from the pool to the callee with the returned future. If the pool can not create or enqueue
   * requests it will fill the returned [[scala.concurrent.Future]] with an
   * [[com.github.mauricio.async.db.pool.PoolExhaustedException]].
   *
   * @return future that will eventually return a usable Connection
   */
  def take: Future[Connection]
  // This can be refined

  /**
   * Returns an object taken from the pool back to it. This object will become available for another client to use.
   * If the object is invalid or can not be reused for some reason the [[scala.concurrent.Future]] returned will contain
   * the error that prevented this object of being added back to the pool. The object is then discarded from the pool.
   *
   * @param connection
   * @return
   */
  def giveBack(connection: Connection): Future[PooledConnection]
  // This must be adapted to the appropriate type, or rejected

  /**
   *
   * Closes this pool and future calls to **take** will cause the [[scala.concurrent.Future]] to raise an
   * [[com.github.mauricio.async.db.pool.PoolAlreadyTerminatedException]].
   *
   * @return
   */

  def close : Future[PooledConnection]

  /**
   *
   * Retrieve and use a connection from the pool for a single computation, returning it when the operation completes.
   *
   * @param f function that uses the connection
   * @return f wrapped with take and giveBack
   */

  def useConnection[A](f: Connection => Future[A])(implicit executionContext: ExecutionContext): Future[A]

}
