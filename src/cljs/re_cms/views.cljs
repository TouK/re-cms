(ns re-cms.views
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [reagent.core :refer [atom]]
            [re-frame.core :as re-frame]
            [reforms.core :as f]))

(defn current-row [data page section]
  (-> @data
      (get @page)
      (get @section)))

(defn content-textarea [value update-fn]
  [:div.col-md-8
   [:div.form-group
    [:textarea {:cols      115
                :rows      10
                :value     value
                :on-change update-fn}]]])

(defn row-editor [data]
  (let [page (reaction (-> @data keys first))
        section (reaction (-> (get @data @page) keys first))
        message (atom nil)]
    (fn []
      [:div.col-md-8
       [:div.row
        [:div.col-md-4
         [:select.form-control {:on-change (fn [e] (reset! page (.. e -target -value)))}
          (for [option (keys @data)]
            ^{:key option} [:option option])]]
        [:div.col-md-4
         [:select.form-control {:on-change (fn [e] (reset! section (.. e -target -value)))}
          (for [option (-> (get @data @page) keys)]
            ^{:key option} [:option option])]]]
       [:div.row
        (content-textarea (:value (current-row data page section))
                          #(println "update"))]
       [:div.row
        [:div.col-md-12
         [:div.form-group
          [:button {:class    "btn"
                    :on-click #(println "on-click")} "Nowa"]
          [:span @message]]]]])))

(defn app []
  (let [content (re-frame/subscribe [:content])]
    (fn []
      [:div
       [:div.page-header [:h1 "CMS"]]
       (f/form
         [row-editor content])])))