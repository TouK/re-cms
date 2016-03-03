(ns re-cms.views
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [reagent.core :refer [atom]]
            [re-frame.core :as re-frame]
            [markdown.core :as md]))

(defn- reset-edit-mode [new]
  (reset! new nil))

(defn- activate-edit-mode [new]
  (reset! new {:page nil :section nil :value nil}))

(defn- select-component [key value-ref options new-row-ref]
  [:div.col-md-6
   (if @new-row-ref
     [:input {:class       "form-control re-input"
              :placeholder (name key)
              :type        "text" :value (get @new-row-ref key)
              :on-change   #(swap! new-row-ref assoc key (-> % .-target .-value))}]
     [:select {:class     "form-control re-input"
               :value     @value-ref
               :on-change #(reset! value-ref (-> % .-target .-value))}
      (for [option options]
        ^{:key option} [:option option])])])

(defn- textarea-component [data preview]
  [:div.col-md-12
   [:textarea.re-editor
    {:cols      115
     :rows      10
     :value     (:value @data)
     :on-change #(do (swap! data assoc :value (-> % .-target .-value))
                     (reset! preview (md/md->html (-> % .-target .-value))))}]])

(defn row-editor [data]
  (let [page (reaction (-> @data keys first))
        section (reaction (-> (get @data @page) keys first))
        row (reaction (-> @data (get @page) (get @section)))
        new-row (atom nil)
        preview (atom nil)
        show-preview (atom false)
        changed (reaction (not= (:value @row)
                                (:value (-> @data (get @page) (get @section)))))
        new-ok (reaction (not (or (nil? (:page @new-row))
                                  (nil? (:section @new-row))
                                  (nil? (:value @new-row)))))]
    (fn []
      [:div
       [:div.row
        (select-component :page page (keys @data) new-row)
        (select-component :section section (-> (get @data @page) keys) new-row)]
       [:div.row
        (textarea-component (if @new-row new-row row) preview)]
       [:div.row
        (when @show-preview
          [:div {:dangerouslySetInnerHTML {:__html @preview}}])
        [:div.col-md-6
         (if @new-row
           [:button.btn.re-button
            {:on-click #(reset-edit-mode new-row)} "Cancel"]
           [:button.btn.re-button
            {:on-click #(activate-edit-mode new-row)} "New entry"])]
        [:div.col-md-6
         (if @new-row
           [:button.btn.re-button.action
            {:class    (when-not @new-ok "disabled")
             :on-click #(do (re-frame/dispatch [:save @new-row])
                            (reset-edit-mode new-row))} "Save"]
           [:button.btn.re-button.action
            {:class    (when-not @changed "disabled")
             :on-click #(re-frame/dispatch [:update @row])} "Update"])]]])))

(defn app []
  (let [content (re-frame/subscribe [:content])]
    (fn []
      [:div.container.cms
       [:img.logo {:src "img/logo.svg"}]
       [row-editor content]])))