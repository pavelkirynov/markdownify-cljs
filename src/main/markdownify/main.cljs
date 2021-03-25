(ns markdownify.main
  (:require [reagent.core :as reagent]
            [reagent.dom :as rd]
            ["showdown" :as showdown]))

(defonce markdown (reagent/atom ""))
(defonce html (reagent/atom ""))
(defonce flash-message (reagent/atom nil))
(defonce flash-timeout (reagent/atom ""))
(defonce showdown-converter (showdown/Converter.))

(defn md->html [md]
      (.makeHtml showdown-converter md))

(defn html->md [html]
      (.makeMarkdown showdown-converter html))

(defn flash
      ([text]
       (flash text 3000))
      ([text ms]
       (when @flash-timeout (js/clearTimeout @flash-timeout))
       (reset! flash-message text)
       (reset! flash-timeout
               (js/setTimeout #(reset! flash-message nil) ms))))

(defn copy-to-clipboard [selector flash-text]
      (let [copy-text (.querySelector js/document selector)]
           (.select copy-text)
           (.execCommand js/document "copy")
           (.blur copy-text)
           (flash flash-text)))

(defn app []
      [:div
       (if @flash-message
         [:div
          {:style {:padding          10
                   :position         :absolute
                   :right            10
                   :top              10
                   :background-color :yellow
                   :border-radius    5}}
          @flash-message]
         nil)
       [:h1
        {:style {:padding 10 :text-align "center"}}
        "Markdownify"]
       [:div
        {:style {:display "flex"}}

        [:div
         {:style {:flex    "1"
                  :padding 10}}
         [:h1 :Markdown]
         [:div
          {:style {:display        "flex"
                   :flex-direction "column"
                   :height         500}}
          [:textarea#initial-markdown
           {:style     {:width  "100%"
                        :resize "none"
                        :flex   1}
            :on-change #(do
                          (reset! markdown (-> % .-target .-value))
                          (reset! html (-> % .-target .-value md->html)))
            :value     @markdown}]
          [:button
           {:style    {:margin-top 10}
            :on-click #(copy-to-clipboard
                         "textarea#initial-markdown"
                         "Markdown copied to clipboard")}
           "Copy Markdown"]]]

        [:div
         {:style {:flex    "1"
                  :padding 10}}
         [:h1 :HTML]
         [:div
          {:style {:display        "flex"
                   :flex-direction "column"
                   :height         500}}
          [:textarea#converted-html
           {:style     {:width  "100%"
                        :resize "none"
                        :flex   1}
            :on-change #(do
                          (reset! html (-> % .-target .-value))
                          (reset! markdown (-> % .-target .-value html->md)))
            :value     @html}]
          [:button
           {:style    {:margin-top 10}
            :on-click #(copy-to-clipboard
                         "textarea#converted-html"
                         "HTML copied to clipboard")}
           "Copy HTML"]]]

        [:div
         {:style {:flex    "1"
                  :padding 10}}
         [:h1 "HTML Preview"]
         [:div
          {:dangerouslySetInnerHTML {:__html @html}
           :style                   {:width       "100%"
                                     :height       500
                                     :border-width "1px"
                                     :border-style :solid
                                     :border-color "rgb(125, 125, 125)"
                                     :overflow     "auto"
                                     :padding      "2px 0px 0px 2px"}}]]]])

(defn mount! []
      (rd/render [app]
                 (.getElementById js/document "app")))

(defn main! []
      (println "Welcome to the app!")
      (mount!))

(defn reload! []
      (println "Reload!!!")
      (mount!))