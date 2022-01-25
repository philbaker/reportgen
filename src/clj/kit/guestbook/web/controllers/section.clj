(ns kit.guestbook.web.controllers.section 
  (:require
   [clojure.tools.logging :as log]
   [kit.guestbook.web.routes.utils :as utils]
   [ring.util.http-response :as http-response]))

(defn save-section!
  [{{:strs [description text]} :form-params :as request}]
  (log/debug "saving section" description text)
  (let [{:keys [query-fn]} (utils/route-data request)]
    (try
      (if (or (empty? description) (empty? text))
        (cond-> (http-response/found "/")
          (empty? description)
          (assoc-in [:flash :errors :description] "description is required")
          (empty? text)
          (assoc-in [:flash :errors :text] "text is required"))
        (do
          (query-fn :save-section! {:description description :text text})
          (http-response/found "/")))
      (catch Exception e
        (log/error e "failed to save section!")
        (-> (http-response/found "/")
            (assoc :flash {:errors {:unknown (.getMessage e)}}))))))
