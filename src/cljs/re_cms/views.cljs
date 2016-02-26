(ns re-cms.views
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [reagent.core :refer [atom]]
            [re-frame.core :as re-frame]
            [reforms.core :as f]))

(defn content-textarea [value update-fn]
  [:div.col-md-8
   [:div.form-group
    [:textarea {:cols      115
                :rows      10
                :value     value
                :on-change update-fn}]]])

(defn- reset-edit-mode [new]
  (reset! new nil))

(defn- activate-edit-mode [new]
  (reset! new {:page "" :section "" :value ""}))

(defn row-editor [data]
  (let [page (reaction (-> @data keys first))
        section (reaction (-> (get @data @page) keys first))
        row (reaction (-> @data (get @page) (get @section)))
        new-row (atom nil)]
    (fn []
      [:div.col-md-8
       [:div.row
        [:div.col-md-4
         (if @new-row
           [:input.form-control {:type "text" :value (:page @new-row)
                                 :on-change #(swap! new-row assoc :page (-> % .-target .-value))}]
           [:select.form-control {:value @page
                                  :on-change #(reset! page (-> % .-target .-value))}
            (for [option (keys @data)]
              ^{:key option} [:option option])])]
        [:div.col-md-4
         (if @new-row
           [:input.form-control {:type "text" :value (:section @new-row)
                                 :on-change #(swap! new-row assoc :section (-> % .-target .-value))}]
           [:select.form-control {:value @section
                                  :on-change #(reset! section (-> % .-target .-value))}
            (for [option (-> (get @data @page) keys)]
              ^{:key option} [:option option])])]]
       [:div.row
        (if @new-row
          (content-textarea (:value @new-row)
                            #(swap! new-row assoc :value (-> % .-target .-value)))
          (content-textarea (:value @row)
                            #(swap! row assoc :value (-> % .-target .-value))))]
       [:div.row
        [:div.col-md-12
         [:div.form-group
          (if @new-row
            [:button {:class    "btn"
                      :on-click #(do (re-frame/dispatch [:save @new-row])
                                     (reset-edit-mode new-row))} "Save"]
            [:button {:class    "btn"
                      :on-click #(re-frame/dispatch [:update @row])} "Update"])
          (if @new-row
            [:button {:class    "btn"
                      :on-click #(reset-edit-mode new-row)} "Cancel"]
            [:button {:class    "btn"
                      :on-click #(activate-edit-mode new-row)} "New entry"])]]]])))

(defn app []
  (let [content (re-frame/subscribe [:content])]
    (fn []
      (println @content)
      [:div
       [:div.page-header [:h1 "CMS"]]
       (f/form
         [row-editor content])])))