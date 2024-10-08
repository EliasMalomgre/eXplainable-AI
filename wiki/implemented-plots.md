# Offered plot types

### Image Based neural nets:
1. [Image Plot](#imagePlot)

### Row/CSV Based neural nets:
1. [Decision Plot](#decisionPlot)
2. [Embedding Plot](#embeddingPlot)
3. [Heatmap Plot](#heatmapPlot)
4. [Scatter Plot](#scatterPlot)
5. [Summary Plot](#summaryPlot)
6. [Waterfall Plot](#waterfallPlot)

## **Image Based neural nets:**

### **Image Plot** <div id="imagePlot"></div>

![Imgee plot](assets/image-plot.png)

The SHAP Image plot shows which areas of an image were the most impactful for the prediction of the model. 

By default, it will show an overlayed heatmap indicating the important areas for each output class and they will be ranked according the order of the output nodes of the neural net. 

Above the images, there will be the predicted value, and class names when provided. Providing the class names allows the classes to be ranked by the highest predicted value and the number of classes shown can be configured. When classes have the same predicted value, it will rank them in the default order.

<ins>How to interpret:</ins> SHAP image plots can be confusing to interpret at first, due to having to understand how SHAP values work and the intricate inner workings of neural networks. Images will be interpreted separately for every output class. The plot algorithm will overlay the regions that led to a positive contribution to said class with red dots, while using blue dots for those that had a negative contribution factor. In a perfect world that would mean we'd be able to easily distinguish important features. But due to the complexity of image recognition neural networks we often see a blur of both colors, since the AI punishes certain important regions that were also apparent in other classes. The plot can help us identify when the neural network wrongly favours certain regions or punishes those it shouldn't. We recommend using similar pictures or changing certain features on the picture through photo editing software, e.g. the background, colour, skin tone.

## **Row/CSV Based neural nets:**

### **Decision Plot** <div id="decisionPlot"></div>

<div align="center" style="background: white">
<img src="./assets/decision-plot-entry.png"/>
</div>

The SHAP decision plot shows how complex models arrive at their predictions by showing the impact of each feature for every single entry. 

It can be used to plot many entries at once or one by one. When requesting this plot multiple results will be created. There will be a plot showing all entries together and one for every entry on its own. When showing them one by one, the values for each feature will be shown in the plot. When the model has multiple output classes, the SHAP values of all classes will be shown on the same plot and only one entry can be shown at a time.

By default neural networks will require text values like (male/female) and (Belgium/France/Netherlands) to be transformed to numeric values, eg. 0, 1, 2 and so on. It's recommended to keep note of what these values stand for, so you can easily interpret the entry plots later.

NOT IMPLEMENTED: A second CSV can be uploaded with the unaltered original values, SHAP will then map the entries to their corresponding row and select the original values to be displayed on the plot.

<ins>How to interpret:</ins> The SHAP decision plot ranks the most important features from top to the least important at the bottom. Each entry plot will start from the bottom at the overall average prediction value, and move up slightly changing its prediction based on every extra feature. At the top we reach the final prediction value, ranging from 0.0 to 1.0. The SHAP decision plot allows us to easily see which features and corresponding values have the largest impact on the decision making.

### **Embedding Plot** <div id="embeddingPlot"></div>

<div align="center" style="background: white">
<img src="./assets/embedding-plot-multi.png"/>
</div>

The SHAP embedding plot projects all the values for a feature, onto a 2-dimensional plot using Principal Component Analysis for visualisation. 

In other words, it will plot all entries in a multi-dimensional space, find the most optimal point of view and use that POV for its 2-dimensional projection. The algorithm uses the SHAP values as a mathematical embedding structure, which we later project to 2D for visualization. This can help us see the spread of different SHAP values for a particular feature.

When requesting this plot, a SHAP embedding plot will be generated for each feature. When there are multiple output classes, one will be generated for every combination of output class and feature.

<ins>How to interpret:</ins> Every feature will result in an 'identical' arrangement of entry-dots, but with a different colour assigned to them. This allows us to see how entries relate to one another, while still displaying how certain features favour separate clusters and punish others. We advise you to compare the different output feature plots with eachother, and use this plot-type to better understand other SHAP plots.

### **Heatmap Plot** <div id="heatmapPlot"></div>

<div align="center" style="background: white">
<img src="./assets/heatmap-plot.png"/>
</div>

The SHAP heatmap plot creates a heatmap to easily visualise the impact of all features.

This plot is designed to show the population substructure of a dataset using supervised clustering and a heatmap. Supervised clustering involves clustering data points not by their original feature values but by their explanations. This plot can be useful to see which ranges are important for all features

When multiple output classes are available, a plot will be generated for each one. This allows you to easily compare how a neural network punishes certain value ranges, that are associated with other classes, while favouring those that are more distinctive for its class.

<ins>How to interpret:</ins> The features are ordered from most influential at the top to least influential at the bottom. The entries are ordered in descending order for every feature from left to right (highest values on the left). The entries are coloured according to how impactful their value for the selected feature was, higher impacts being coloured in red, while negative contributions in blue.

### **Scatter Plot** <div id="scatterPlot"></div>

<div align="center" style="background: white">
<img src="./assets/scatter-plot.png"/>
</div>


The SHAP scatter plot shows the effect a single feature has on the predictions made by the model.

Each dot represents a single entry from the dataset. The x-axis is the value of the feature. The y-axis is the SHAP value for that feature, which represents the impact of that value on the final prediction. The light grey area at the bottom of the plot is a histogram showing the distribution of data values. This plot can be useful to see the distribution of all the values and which ranges are important.

When requesting this plot, a SHAP scatter plot will be generated for each feature. When there are multiple output classes, one will be generated for every combination of output class and feature.

<ins>How to interpret:</ins> We recommend comparing the different output plots to see the impact of all features, while analysing the important ranges, trends and clusters to check for any unexpected behaviour or outliers. It can also help you quickly analyse the distribution of your given values.

### **Summary Plot** <div id="summaryPlot"></div>

<div align="center" style="background: white">
<img src="./assets/summary-plot.png"/>
</div>


The SHAP summary/beeswarm plot shows the distribution of impactful clusters per feature.

Creates a SHAP beeswarm plot or bar plot. This plot shows the distribution and importance of each feature and can be used to see a clear summary of all features.

In case there are multiple output classes, a separate plot will be generated for each one of them.

<ins>How to interpret:</ins> The features are ordered from most influential at the top, to least influential at the bottom. This plot can be used to quickly analyse the importance of features and distinguish clusters within a feature. In contrast to the usual SHAP plots, the reds and blues here do not correspond with their SHAP values, but instead with their actual values, blues are smaller than the average and reds bigger. The dots/entries here are ordered by their SHAP value from left to right, left having a negative contribution and right a positive. When used for different output classes it can help identify important distinctive clusters, unexpected behaviour and outliers.

### **Waterfall Plot** <div id="waterfallPlot"></div>


<div align="center" style="background: white">
<img src="./assets/waterfall-plot.png"/>
</div>


The SHAP waterfall plot can be used to visualise the decision making process for a single entry.

Waterfall plots are designed to display explanations for individual entries. The bottom of a waterfall plot starts as the expected average prediction of the neural network's output. Each row shows how the positive (red) or negative (blue) contribution for the value of that feature moves the expected prediction to the final prediction.

When requesting this plot, a SHAP scatter plot will be generated for each entry. When there are multiple output classes, one will be generated for every combination of entry and output class.

By default neural networks will require text values like (male/female) and (Belgium/France/Netherlands) to be transformed to numeric values, eg. 0, 1, 2 and so on. It's recommended to keep note of what these values stand for, so you can easily interpret the entry plots later.

NOT IMPLEMENTED: A second CSV can be uploaded with the unaltered original values, SHAP will then map the entries to their corresponding row and select the original values to be displayed on the plot.

<ins>How to interpret:</ins> The SHAP Waterfall plot ranks the most important features from top to the least important at the bottom. Each entry plot will start from the bottom at the overall average prediction value, and move up slightly changing its prediction based on every extra feature. At the top we reach the final prediction value, ranging from 0.0 to 1.0. The SHAP Waterfall plot allows us to easily see which features and corresponding values have the largest impact on the decision making.
