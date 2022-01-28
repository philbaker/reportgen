(ns kit.reportgen.web.controllers.section 
  (:require
   [clojure.tools.logging :as log]
   [kit.reportgen.web.routes.utils :as utils]
   [ring.util.http-response :as http-response]))

(defn save-section!
  [{{:strs [description text]} :form-params :as request}]
  (log/debug "saving section" description text)
  (let [{:keys [query-fn]} (utils/route-data request)]
    (try
      (if (or (empty? description) (empty? text))
        (cond-> (http-response/found "/section")
          (empty? description)
          (assoc-in [:flash :errors :description] "description is required")
          (empty? text)
          (assoc-in [:flash :errors :text] "text is required"))
        (do
          (query-fn :save-section! {:description description :text text})
          (http-response/found "/section")))
      (catch Exception e
        (log/error e "failed to save section!")
        (-> (http-response/found "/section")
            (assoc :flash {:errors {:unknown (.getMessage e)}}))))))

(defn update-section!
  [{{:strs [id description text]} :form-params :as request}]
  (log/debug "updating section" id description text)
  (let [{:keys [query-fn]} (utils/route-data request)]
    (try
      (if (or (empty? description) (empty? text))
        (cond-> (http-response/found (str "/section/" id))
          (empty? description)
          (assoc-in [:flash :errors :description] "description is required")
          (empty? text)
          (assoc-in [:flash :errors :text] "text is required"))
        (do
          (query-fn :update-section! {:id id :description description :text text})
          (http-response/found (str "/section/" id))))
      (catch Exception e
        (log/error e "failed to update section!")
        (-> (http-response/found (str "/section/" id))
            (assoc :flash {:errors {:unknown (.getMessage e)}}))))))

(defn delete-section!
  [{{:strs [id]} :form-params :as request}]
  (log/debug "deleting section" id)
  (let [{:keys [query-fn]} (utils/route-data request)]
    (try
      (query-fn :delete-section! {:id id})
      (http-response/found "/section")
      (catch Exception e
        (log/error e "failed to delete section!")
        (-> (http-response/found "/section")
            (assoc :flash {:errors {:unknown (.getMessage e)}}))))))
