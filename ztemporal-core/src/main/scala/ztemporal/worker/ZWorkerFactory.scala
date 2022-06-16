package ztemporal.worker

import io.temporal.worker.WorkerFactory
import zio._
import ztemporal.workflow.ZWorkflowClient

/** Maintains worker creation and lifecycle.
  *
  * @see
  *   [[WorkerFactory]]
  */
class ZWorkerFactory private (private val self: WorkerFactory) extends AnyVal {

  /** Starts all the workers created by this factory.
    */
  def start: UIO[Unit] =
    UIO.effectTotal(self.start())

  /** Initiates an orderly shutdown in which polls are stopped and already received workflow and activity tasks are
    * executed.
    *
    * @see
    *   [[WorkerFactory#shutdown]]
    */
  def shutdown: UIO[Unit] =
    UIO.effectTotal(self.shutdown())

  /** Initiates an orderly shutdown in which polls are stopped and already received workflow and activity tasks are
    * attempted to be stopped. This implementation cancels tasks via Thread.interrupt(), so any task that fails to
    * respond to interrupts may never terminate.
    *
    * @see
    *   [[WorkerFactory#shutdownNow]]
    */
  def shutdownNow: UIO[Unit] =
    UIO.effectTotal(self.shutdownNow())

  /** Creates worker that connects to an instance of the Temporal Service. It uses the namespace configured at the
    * Factory level. New workers cannot be created after the start() has been called
    *
    * @see
    *   [[WorkerFactory#newWorker]]
    * @param taskQueue
    *   task queue name worker uses to poll. It uses this name for both workflow and activity task queue polls.
    * @param options
    *   Options for configuring worker.
    * @return
    *   ZWorker
    */
  def newWorker(taskQueue: String, options: ZWorkerOptions = ZWorkerOptions.default): UIO[ZWorker] =
    UIO.effectTotal(new ZWorker(self.newWorker(taskQueue, options.toJava), Nil, Nil))

}

object ZWorkerFactory {

  /** Creates an instance of [[ZWorkerFactory]]
    *
    * @see
    *   [[WorkerFactory.newInstance]]
    *
    * @param client
    *   temporal client
    * @param options
    *   worker factory option
    * @return
    *   new worker factory
    */
  def make(
    client:  ZWorkflowClient,
    options: ZWorkerFactoryOptions = ZWorkerFactoryOptions.default
  ): UIO[ZWorkerFactory] =
    UIO.effectTotal(
      new ZWorkerFactory(WorkerFactory.newInstance(client.self, options.toJava))
    )
}
