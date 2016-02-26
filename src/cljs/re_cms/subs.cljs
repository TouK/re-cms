(ns re-cms.subs
    (:require-macros [reagent.ratom :refer [reaction]])
    (:require [re-frame.core :as re-frame]
              [plumbing.core :refer [map-vals]]))

(defn- page->section-mapping [page]
  (->> page
       (group-by :section)
       (map-vals first)))

(re-frame/register-sub
  :content
  (fn [db]
    (reaction (->> @db
                   :values
                   (group-by :page)
                   (map-vals page->section-mapping)))))
