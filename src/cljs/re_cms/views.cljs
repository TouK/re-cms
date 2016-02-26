(ns re-cms.views
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [reagent.core :refer [atom]]
            [re-frame.core :as re-frame]
            [markdown.core :as md]))

(defn- reset-edit-mode [new changed]
  (reset! changed false)
  (reset! new nil))

(defn- activate-edit-mode [new changed]
  (reset! changed false)
  (reset! new {:page "" :section "" :value ""}))

(defn- select-component [key value-ref options new-row-ref changed]
  [:div.col-md-6
   (if @new-row-ref
     [:input {:class     "form-control re-input"
              :type      "text" :value (get @new-row-ref key)
              :on-change #(do (swap! new-row-ref assoc key (-> % .-target .-value))
                              (reset! changed false))}]
     [:select {:class     "form-control re-input"
               :value     @value-ref
               :on-change #(do (reset! value-ref (-> % .-target .-value))
                               (reset! changed false))}
      (for [option options]
        ^{:key option} [:option option])])])

(defn- textarea-component [data preview changed]
  [:div.col-md-12
   [:textarea.re-editor
    {:cols      115
     :rows      10
     :value     (:value @data)
     :on-change #(do (swap! data assoc :value (-> % .-target .-value))
                     (reset! preview (md/md->html (-> % .-target .-value)))
                     (reset! changed true))}]])

(defn row-editor [data]
  (let [page (reaction (-> @data keys first))
        section (reaction (-> (get @data @page) keys first))
        row (reaction (-> @data (get @page) (get @section)))
        new-row (atom nil)
        preview (atom nil)
        show-preview (atom false)
        changed (atom false)]
    (fn []
      [:div
       [:div.row
        (select-component :page page (keys @data) new-row changed)
        (select-component :section section (-> (get @data @page) keys) new-row changed)]
       [:div.row
        (textarea-component (if @new-row new-row row) preview changed)]
       [:div.row
        (when @show-preview
          [:div {:dangerouslySetInnerHTML {:__html @preview}}])
        [:div.col-md-6
         (if @new-row
           [:button.btn.re-button
            {:on-click #(reset-edit-mode new-row changed)} "Cancel"]
           [:button.btn.re-button
            {:on-click #(activate-edit-mode new-row changed)} "New entry"])]
        [:div.col-md-6
         (if @new-row
           [:button.btn.re-button.action
            {:class (when-not @changed "disabled")
             :on-click #(do (re-frame/dispatch [:save @new-row])
                            (reset-edit-mode new-row changed))} "Save"]
           [:button.btn.re-button.action
            {:class    (when-not @changed "disabled")
             :on-click #(do (re-frame/dispatch [:update @row])
                            (reset! changed false))} "Update"])]]])))

(defn app []
  (let [content (re-frame/subscribe [:content])]
    (fn []
      [:div.container.cms
       [:img.logo {:src "img/logo.svg"}]
       [row-editor content]])))