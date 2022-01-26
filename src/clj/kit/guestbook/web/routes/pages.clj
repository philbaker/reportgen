(ns kit.guestbook.web.routes.pages
  (:require
   [clojure.tools.logging :as log]
   [kit.guestbook.web.middleware.exception :as exception]
   [kit.guestbook.web.pages.layout :as layout]
   [integrant.core :as ig]
   [reitit.ring.middleware.muuntaja :as muuntaja]
   [reitit.ring.middleware.parameters :as parameters]
   [ring.middleware.anti-forgery :refer [wrap-anti-forgery]]
   [kit.guestbook.web.routes.utils :as utils]
   [kit.guestbook.web.controllers.guestbook :as guestbook]
   [kit.guestbook.web.controllers.section :as section]))

(defn wrap-page-defaults []
  (let [error-page (layout/error-page
                     {:status 403
                      :title "Invalid anti-forgery token"})]
    #(wrap-anti-forgery % {:error-response error-page})))

(defn home [{:keys [flash] :as request}]
  (let [{:keys [query-fn]} (utils/route-data request)]
    (layout/render request "home.html" {:messages (query-fn :get-messages {})
                                        :sections (query-fn :get-sections {})
                                        :errors (:errors flash)})))

(defn report [{:keys [path-params] :as request}]
  (let [{:keys [query-fn]} (utils/route-data request)]
    (layout/render request "report.html" {:report (first (query-fn :get-report {:id (:id path-params)}))})))

;; Routes
(defn page-routes [_opts]
  [["/" {:get home}]
   ["/report/:id" {:get report}]
   ["/save-message" {:post guestbook/save-message!}]
   ["/save-section" {:post section/save-section!}]])

(defn route-data [opts]
  (merge
   opts
   {:middleware
    [;; Default middleware for pages
     (wrap-page-defaults)
     ;; query-params & form-params
     parameters/parameters-middleware
     ;; encoding response body
     muuntaja/format-response-middleware
     ;; exception handling
     exception/wrap-exception]}))

(derive :reitit.routes/pages :reitit/routes)

(defmethod ig/init-key :reitit.routes/pages
  [_ {:keys [base-path]
      :or   {base-path ""}
      :as   opts}]
  (layout/init-selmer!)
  [base-path (route-data opts) (page-routes opts)])
