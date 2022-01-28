(ns kit.reportgen.web.routes.pages
  (:require
   [kit.reportgen.web.middleware.exception :as exception]
   [kit.reportgen.web.pages.layout :as layout]
   [integrant.core :as ig]
   [reitit.ring.middleware.muuntaja :as muuntaja]
   [reitit.ring.middleware.parameters :as parameters]
   [ring.middleware.anti-forgery :refer [wrap-anti-forgery]]
   [kit.reportgen.web.routes.utils :as utils]
   [kit.reportgen.web.controllers.report :as report]
   [kit.reportgen.web.controllers.section :as section]))

(defn wrap-page-defaults []
  (let [error-page (layout/error-page
                     {:status 403
                      :title "Invalid anti-forgery token"})]
    #(wrap-anti-forgery % {:error-response error-page})))

(defn report [{:keys [path-params] :as request}]
  (let [{:keys [query-fn]} (utils/route-data request)
        report (first (query-fn :get-report {:id (:id path-params)}))]
    (if report
      (layout/render request "report.html" {:report report})
      (layout/error-page  {:status 404
                           :title "Page not found"}))))

(defn report-edit [{:keys [path-params] :as request}]
  (let [{:keys [query-fn]} (utils/route-data request)
        report (first (query-fn :get-report {:id (:id path-params)}))]
    (if report
      (layout/render request "report-edit.html" {:report report})
      (layout/error-page  {:status 404
                           :title "Page not found"}))))

(defn reports [{:keys [flash] :as request}]
  (let [{:keys [query-fn]} (utils/route-data request)]
    (layout/render request "reports.html" {:reports(query-fn :get-reports {})
                                           :sections (query-fn :get-sections {})
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
  [["/" {:get reports}]
   ["/section" {:get sections}]
   ["/report/:id" {:get report}]
   ["/report/:id/edit" {:get report-edit}]
   ["/section/:id" {:get section}]
   ["/section/:id/edit" {:get section-edit}]
   ["/save-report" {:post report/save-report!}]
   ["/update-report" {:post report/update-report!}]
   ["/delete-report" {:post report/delete-report!}]
   ["/save-section" {:post section/save-section!}]
   ["/update-section" {:post section/update-section!}]
   ["/delete-section" {:post section/delete-section!}]])

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
