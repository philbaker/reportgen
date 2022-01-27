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
   [kit.guestbook.web.controllers.report :as report]
   [kit.guestbook.web.controllers.section :as section]))

(defn wrap-page-defaults []
  (let [error-page (layout/error-page
                     {:status 403
                      :title "Invalid anti-forgery token"})]
    #(wrap-anti-forgery % {:error-response error-page})))

(defn home [{:keys [flash] :as request}]
  (let [{:keys [query-fn]} (utils/route-data request)]
    (layout/render request "home.html" {:messages (query-fn :get-messages {})
                                        :errors (:errors flash)})))

(defn report [{:keys [path-params] :as request}]
  (let [{:keys [query-fn]} (utils/route-data request)
        report (first (query-fn :get-report {:id (:id path-params)}))]
    (if report
      (layout/render request "report.html" {:report report})
      (layout/error-page  {:status 404
                           :title "Page not found"}))))

(defn reports [{:keys [flash] :as request}]
  (let [{:keys [query-fn]} (utils/route-data request)]
    (layout/render request "reports.html" {:reports(query-fn :get-reports {})
                                           :errors (:errors flash)})))

(defn section [{:keys [path-params] :as request}]
  (let [{:keys [query-fn]} (utils/route-data request)
        section (first (query-fn :get-section {:id (:id path-params)}))]
    (if section
      (layout/render request "section.html" {:section section})
      (layout/error-page  {:status 404
                           :title "Page not found"}))))

(defn section-edit [{:keys [path-params] :as request}]
  (let [{:keys [query-fn]} (utils/route-data request)
        section (first (query-fn :get-section {:id (:id path-params)}))]
    (if section
      (layout/render request "section-edit.html" {:section section})
      (layout/error-page  {:status 404
                           :title "Page not found"}))))

(defn sections [{:keys [flash] :as request}]
  (let [{:keys [query-fn]} (utils/route-data request)]
    (layout/render request "sections.html" {:sections (query-fn :get-sections {})
                                            :errors (:errors flash)})))

;; Routes
(defn page-routes [_opts]
  [["/" {:get home}]
   ["/section" {:get sections}]
   ["/report" {:get reports}]
   ["/report/:id" {:get report}]
   ["/section/:id" {:get section}]
   ["/section/:id/edit" {:get section-edit}]
   ["/save-message" {:post guestbook/save-message!}]
   ["/save-report" {:post report/save-report!}]
   ["/save-section" {:post section/save-section!}]
   ["/update-section" {:post section/update-section!}]])

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
