(ns kit.guestbook.web.controllers.report
  (:require
   [clojure.tools.logging :as log]
   [kit.guestbook.web.routes.utils :as utils]
   [ring.util.http-response :as http-response]))

(defn save-report!
  [{{:strs [section-1-title, section-1-text, section-2-title, section-2-text, section-3-title, section-3-text, section-4-title, section-4-text]} :form-params :as request}]
  (log/debug "saving report" section-1-title, section-1-text, section-2-title, section-2-text, section-3-title, section-3-text, section-4-title, section-4-text)
  (let [{:keys [query-fn]} (utils/route-data request)]
    (try
      (if (or (empty? section-1-title) (empty? section-1-text) (empty? section-2-title) (empty? section-2-text) (empty? section-3-title) (empty? section-3-text) (empty? section-4-title) (empty? section-4-text))
        (cond-> (http-response/found "/report")
          (empty? section-1-title)
          (assoc-in [:flash :errors :section-1-title] "title is required")
          (empty? section-1-text)
          (assoc-in [:flash :errors :section-1-text] "text is required")
          (empty? section-2-title)
          (assoc-in [:flash :errors :section-2-title] "title is required")
          (empty? section-2-text)
          (assoc-in [:flash :errors :section-2-text] "text is required")
          (empty? section-3-title)
          (assoc-in [:flash :errors :section-3-title] "title is required")
          (empty? section-3-text)
          (assoc-in [:flash :errors :section-3-text] "text is required")
          (empty? section-4-title)
          (assoc-in [:flash :errors :section-4-title] "title is required")
          (empty? section-4-text)
          (assoc-in [:flash :errors :section-4-text] "text is required"))
        (do
          (query-fn :save-report! {:section-1-title section-1-title :section-1-text section-1-text :section-2-title section-2-title :section-2-text section-2-text :section-3-title section-3-title :section-3-text section-3-text :section-4-title section-4-title :section-4-text section-4-text})
          (http-response/found "/report")))
      (catch Exception e
        (log/error e "failed to save report!")
        (-> (http-response/found "/report")
            (assoc :flash {:errors {:unknown (.getMessage e)}}))))))
