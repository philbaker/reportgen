(ns kit.reportgen.web.controllers.report
  (:require
   [clojure.tools.logging :as log]
   [clojure.string :refer [trim]]
   [kit.reportgen.web.routes.utils :as utils]
   [ring.util.http-response :as http-response]))

(defn save-report!
  [{{:strs [section-1-title, section-1-text, section-2-title, section-2-text, section-3-title, section-3-text, section-4-title, section-4-text]} :form-params :as request}]
  (log/debug "saving report" section-1-title, section-1-text, section-2-title, section-2-text, section-3-title, section-3-text, section-4-title, section-4-text)
  (let [{:keys [query-fn]} (utils/route-data request)]
    (try
      (if (or (empty? (trim section-1-title))
              (empty? (trim section-1-text))
              (empty? (trim section-2-title))
              (empty? (trim section-2-text))
              (empty? (trim section-3-title))
              (empty? (trim section-3-text))
              (empty? (trim section-4-title))
              (empty? (trim section-4-text)))
        (http-response/found "/")
        (do
          (query-fn :save-report! {:section-1-title section-1-title :section-1-text section-1-text :section-2-title section-2-title :section-2-text section-2-text :section-3-title section-3-title :section-3-text section-3-text :section-4-title section-4-title :section-4-text section-4-text})
          (http-response/found "/")))
      (catch Exception e
        (log/error e "failed to save report!")
        (-> (http-response/found "/")
            (assoc :flash {:errors {:unknown (.getMessage e)}}))))))

(defn update-report!
  [{{:strs [id section-1-title, section-1-text, section-2-title, section-2-text, section-3-title, section-3-text, section-4-title, section-4-text]} :form-params :as request}]
  (log/debug "updating report" id section-1-title, section-1-text, section-2-title, section-2-text, section-3-title, section-3-text, section-4-title, section-4-text)
  (let [{:keys [query-fn]} (utils/route-data request)]
    (try
      (if (or (empty? (trim section-1-title))
              (empty? (trim section-1-text))
              (empty? (trim section-2-title))
              (empty? (trim section-2-text))
              (empty? (trim section-3-title))
              (empty? (trim section-3-text))
              (empty? (trim section-4-title))
              (empty? (trim section-4-text)))
        (http-response/found "/")
        (do
          (query-fn :update-report! {:id id :section-1-title section-1-title :section-1-text section-1-text :section-2-title section-2-title :section-2-text section-2-text :section-3-title section-3-title :section-3-text section-3-text :section-4-title section-4-title :section-4-text section-4-text})
          (http-response/found (str "/report/" id))))
      (catch Exception e
        (log/error e "failed to update report!")
        (-> (http-response/found (str "/report/" id))
            (assoc :flash {:errors {:unknown (.getMessage e)}}))))))

(defn delete-report!
  [{{:strs [id]} :form-params :as request}]
  (log/debug "deleting report" id)
  (let [{:keys [query-fn]} (utils/route-data request)]
    (try
      (query-fn :delete-report! {:id id})
      (http-response/found "/")
      (catch Exception e
        (log/error e "failed to delete report!")
        (-> (http-response/found (str "/report/" id))
            (assoc :flash {:errors {:unknown (.getMessage e)}}))))))
