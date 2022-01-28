(ns kit.reportgen.core
  (:require
    [clojure.tools.logging :as log]
    [integrant.core :as ig]
    [kit.reportgen.config :as config]
    [kit.reportgen.env :refer [defaults]]

    ;; Edges        
    [kit.edge.utils.repl] 
    [kit.edge.server.undertow]
    [kit.reportgen.web.handler]

    ;; Routes
    [kit.reportgen.web.routes.api]
    
    [kit.reportgen.web.routes.pages] 
    [kit.edge.db.sql])
  (:gen-class))

;; log uncaught exceptions in threads
(Thread/setDefaultUncaughtExceptionHandler
  (reify Thread$UncaughtExceptionHandler
    (uncaughtException [_ thread ex]
      (log/error {:what :uncaught-exception
                  :exception ex
                  :where (str "Uncaught exception on" (.getName thread))}))))

(defonce system (atom nil))

(defn stop-app []
  ((or (:stop defaults) (fn [])))
  (some-> (deref system) (ig/halt!))
  (shutdown-agents))

(defn start-app [& [params]]
  ((or (:start params) (:start defaults) (fn [])))
  (->> (config/system-config (or (:opts params) (:opts defaults) {}))
       (ig/prep)
       (ig/init)
       (reset! system))
  (.addShutdownHook (Runtime/getRuntime) (Thread. stop-app)))

(defn -main [& _]
  (start-app))

