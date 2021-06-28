<template>
  <!-- TODO needs to receive a proper clean up, was quickly made -->
  <div class="row items-center" v-show="plotType !== null">
    <div class="col-lg-6 col-sm-12 q-pr-sm">
      <div v-show="plotType !== null">
        <div v-show="plotType === 'DECISION'">
          <p>
            The SHAP decision plot shows how complex models arrive at their predictions by showing the impact of each
            feature for every single entry.
          </p>
          <p>
            It can be used to plot many entries at once or one by one. When requesting this plot multiple results will
            be created. There will be a plot showing all entries together and one for every entry on its own. When
            showing them one by one, the values for each feature will be shown in the plot. When the model has multiple
            output classes, the SHAP values of all classes will be shown on the same plot and only one entry can be
            shown at a time.
          </p>
          <p>
            By default neural networks will require text values like (male/female) and (Belgium/France/Netherlands) to
            be transformed to numeric values, eg. 0, 1, 2 and so on. It's recommended to keep note of what these values
            stand for, so you can easily interpret the entry plots later.
          </p>
          <p v-if="false">
            <!-- Not Implemented -->
            A second CSV can be uploaded with the unaltered original values, SHAP will then map the entries to their
            corresponding row and select the original values to be displayed on the plot.
          </p>
          <p>
            <ins>How to interpret:</ins>
            The SHAP decision plot ranks the most important features from top to the least important at the bottom. Each
            entry plot will start from the bottom at the overall average prediction value, and move up slightly changing
            its prediction based on every extra feature. At the top we reach the final prediction value, ranging from
            0.0 to 1.0. The SHAP decision plot allows us to easily see which features and corresponding values have the
            largest impact on the decision making.
          </p>
        </div>
        <div v-show="plotType === 'IMAGE'">
          <p>
            The SHAP Image plot shows which areas of an image were the most impactful for the prediction of the model.
          </p>
          <p>
            By default, it will show an overlayed heatmap indicating the important areas for each output class and they
            will be ranked according the order of the output nodes of the neural net.
          </p>
          <p>
            Above the images, there will be the predicted value, and class names when provided. Providing the class
            names allows the classes to be ranked by the highest predicted value and the number of classes shown can be
            configured. When classes have the same predicted value, it will rank them in the default order.
          </p>
          <p>
            <ins>How to interpret:</ins>
            SHAP image plots can be confusing to interpret at first, due to having to understand how SHAP values work
            and the intricate inner workings of neural networks. Images will be interpreted separately for every output
            class. The plot algorithm will overlay the regions that led to a positive contribution to said class with
            red dots, while using blue dots for those that had a negative contribution factor. In a perfect world that
            would mean we'd be able to easily distinguish important features. But due to the complexity of image
            recognition neural networks we often see a blur of both colors, since the AI punishes certain important
            regions that were also apparent in other classes. The plot can help us identify when the neural network
            wrongly favours certain regions or punishes those it shouldn't. We recommend using similar pictures or
            changing certain features on the picture through photo editing software, e.g. the background, colour, skin
            tone.
          </p>
        </div>
        <div v-show="plotType === 'SCATTER'">
          <p>
            The SHAP scatter plot shows the effect a single feature has on the predictions made by the model.
          </p>
          <p>
            Each dot represents a single entry from the dataset. The x-axis is the value of the feature. The y-axis is
            the SHAP value for that feature, which represents the impact of that value on the final prediction. The
            light grey area at the bottom of the plot is a histogram showing the distribution of data values. This plot
            can be useful to see the distribution of all the values and which ranges are important.
          </p>
          <p>
            When requesting this plot, a SHAP scatter plot will be generated for each feature. When there are multiple
            output classes, one will be generated for every combination of output class and feature.
          </p>
          <p>
            <ins>How to interpret:</ins>
            We recommend comparing the different output plots to see the impact of all features, while analysing the
            important ranges, trends and clusters to check for any unexpected behaviour or outliers. It can also help
            you quickly analyse the distribution of your given values.
          </p>
        </div>
        <div v-show="plotType === 'SUMMARY'">
          <p>
            The SHAP summary/beeswarm plot shows the distribution of impactful clusters per feature.
          </p>
          <p>
            Creates a SHAP beeswarm plot or bar plot. This plot shows the distribution and importance of each feature
            and can be used to see a clear summary of all features.
          </p>
          <p>
            In case there are multiple output classes, a separate plot will be generated for each one of them.
          </p>
          <p>
            <ins>How to interpret:</ins>
            The features are ordered from most influential at the top, to least influential at the bottom. This plot can
            be used to quickly analyse the importance of features and distinguish clusters within a feature. In contrast
            to the usual SHAP plots, the reds and blues here do not correspond with their SHAP values, but instead with
            their actual values, blues are smaller than the average and reds bigger. The dots/entries here are ordered
            by their SHAP value from left to right, left having a negative contribution and right a positive. When used
            for different output classes it can help identify important distinctive clusters, unexpected behaviour and
            outliers.
          </p>
        </div>
        <div v-show="plotType === 'WATERFALL'">
          <p>
            The SHAP waterfall plot can be used to visualise the decision making process for a single entry.
          </p>
          <p>
            Waterfall plots are designed to display explanations for individual entries. The bottom of a waterfall plot
            starts as the expected average prediction of the neural network's output. Each row shows how the positive
            (red) or negative (blue) contribution for the value of that feature moves the expected prediction to the
            final prediction.
          </p>
          <p>
            When requesting this plot, a SHAP scatter plot will be generated for each entry. When there are multiple
            output classes, one will be generated for every combination of entry and output class.
          </p>
          <p>
            By default neural networks will require text values like (male/female) and (Belgium/France/Netherlands) to
            be transformed to numeric values, eg. 0, 1, 2 and so on. It's recommended to keep note of what these values
            stand for, so you can easily interpret the entry plots later.
          </p>
          <p v-if="false">
            <!-- Not Implemented -->
            A second CSV can be uploaded with the unaltered original values, SHAP will then map the entries to their
            corresponding row and select the original values to be displayed on the plot.
          </p>
          <p>
            <ins>How to interpret:</ins>
            The SHAP Waterfall plot ranks the most important features from top to the least important at the bottom.
            Each entry plot will start from the bottom at the overall average prediction value, and move up slightly
            changing its prediction based on every extra feature. At the top we reach the final prediction value,
            ranging from 0.0 to 1.0. The SHAP Waterfall plot allows us to easily see which features and corresponding
            values have the largest impact on the decision making.
          </p>
        </div>
        <div v-show="plotType === 'HEATMAP'">
          <p>
            The SHAP heatmap plot creates a heatmap to easily visualise the impact of all features.
          </p>
          <p>
            This plot is designed to show the population substructure of a dataset using supervised clustering and a
            heatmap. Supervised clustering involves clustering data points not by their original feature values but by
            their explanations. This plot can be useful to see which ranges are important for all features
          </p>
          <p>
            When multiple output classes are available, a plot will be generated for each one. This allows you to easily
            compare how a neural network punishes certain value ranges, that are associated with other classes, while
            favouring those that are more distinctive for its class.
          </p>
          <p>
            <ins>How to interpret:</ins>
            The features are ordered from most influential at the top to least influential at the bottom. The entries
            are ordered in descending order for every feature from left to right (highest values on the left). The
            entries are coloured according to how impactful their value for the selected feature was, higher impacts
            being coloured in red, while negative contributions in blue.
          </p>
        </div>
        <div v-show="plotType === 'EMBEDDING'">
          <p>
            The SHAP embedding plot projects all the values for a feature, onto a 2-dimensional plot using Principal
            Component Analysis for visualisation.
          </p>
          <p>
            In other words, it will plot all entries in a multi-dimensional space, find the most optimal point of view
            and use that POV for its 2-dimensional projection. The algorithm uses the SHAP values as a mathematical
            embedding structure, which we later project to 2D for visualization. This can help us see the spread of
            different SHAP values for a particular feature.
          </p>
          <p>
            When requesting this plot, a SHAP embedding plot will be generated for each feature. When there are multiple
            output classes, one will be generated for every combination of output class and feature.
          </p>
          <p>
            <ins>How to interpret:</ins>
            Every feature will result in an 'identical' arrangement of entry-dots, but with a different colour assigned
            to them. This allows us to see how entries relate to one another, while still displaying how certain
            features favour separate clusters and punish others. We advise you to compare the different output feature
            plots with eachother, and use this plot-type to better understand other SHAP plots.
          </p>
        </div>
      </div>
    </div>

    <div class="col-lg-6 col-sm-12">

      <q-carousel
          v-if="plotType === 'DECISION'"
          animated
          v-model="slideDec"
          navigation
          control-color="primary"
          infinite
          :autoplay="true"
          transition-prev="slide-right"
          transition-next="slide-left"
          keep-alive
      >
        <q-carousel-slide name="1dec" class="row items-center">
          <img src="../../assets/images/plots/decision_general.png" class="example-image"
               alt="Decision plot for all data"/>
        </q-carousel-slide>
        <q-carousel-slide name="2dec" class="row items-center">
          <img src="../../assets/images/plots/decision_entry.png" class="example-image"
               alt="Decision plot for a single entry"/>
        </q-carousel-slide>
        <q-carousel-slide name="3dec" class="row items-center">
          <img src="../../assets/images/plots/decision_multi.png" class="example-image"
               alt="Decision plot for the output class of an entry in a multiclassifier network"/>
        </q-carousel-slide>
      </q-carousel>

      <q-carousel
          v-if="plotType === 'IMAGE'"
          animated
          v-model="slideIma"
          navigation
          control-color="primary"
          infinite
          :autoplay="true"
          transition-prev="slide-right"
          transition-next="slide-left"
          keep-alive
      >
        <q-carousel-slide name="1ima" class="row items-center">
          <img src="../../assets/images/plots/image_vgg16.png" class="example-image"
               alt="Image plot with ranked output for imagenet"/>
        </q-carousel-slide>
        <q-carousel-slide name="2ima" class="row items-center">
          <img src="../../assets/images/plots/image_vgg16_planes.png" class="example-image"
               alt="Image plot displaying how you can use photo-editing software to get interesting results"/>
        </q-carousel-slide>
        <q-carousel-slide name="3ima" class="row items-center">
          <img src="../../assets/images/plots/image_wolves.png" class="example-image"
               alt="Image plot showcasing a situation where identification goes wrong"/>
        </q-carousel-slide>
      </q-carousel>

      <q-carousel
          v-if="plotType === 'SCATTER'"
          animated
          v-model="slideSca"
      >
        <q-carousel-slide name="1sca" class="q-pa-md row items-center">
          <img src="../../assets/images/plots/scatter.png" class="example-image"
               alt="Scatter plot for the feature Age in an Adult-Income AI"/>
        </q-carousel-slide>
      </q-carousel>

      <q-carousel
          v-if="plotType === 'SUMMARY'"
          animated
          v-model="slideSum"
          navigation
          control-color="primary"
          infinite
          :autoplay="true"
          transition-prev="slide-right"
          transition-next="slide-left"
          keep-alive
      >
        <q-carousel-slide name="1sum" class="row items-center">
          <img src="../../assets/images/plots/summary_general.png" class="example-image"
               alt="Summary plot for all entries"/>
        </q-carousel-slide>
        <q-carousel-slide name="2sum" class="row items-center">
          <img src="../../assets/images/plots/summary_combined.png" class="example-image"
               alt="Summary plot for the combined class data"/>
        </q-carousel-slide>
        <q-carousel-slide name="3sum" class="row items-center">
          <img src="../../assets/images/plots/summary_class.png" class="example-image"
               alt="Summary plot for the output class in a multiclassifier network"/>
        </q-carousel-slide>
      </q-carousel>

      <q-carousel
          v-if="plotType === 'WATERFALL'"
          animated
          v-model="slideWat"
          navigation
          control-color="primary"
          infinite
          :autoplay="true"
          transition-prev="slide-right"
          transition-next="slide-left"
          keep-alive
      >
        <q-carousel-slide name="1wat" class="row items-center">
          <img src="../../assets/images/plots/waterfall_entry.png" class="example-image"
               alt="Waterfall plot for a single entry"/>
        </q-carousel-slide>
        <q-carousel-slide name="2wat" class="row items-center">
          <img src="../../assets/images/plots/waterfall_class.png" class="example-image"
               alt="Waterfall plot for an entry in a multiclassifier network"/>
        </q-carousel-slide>
      </q-carousel>

      <q-carousel
          v-if="plotType === 'HEATMAP'"
          animated
          v-model="slideHea"
          navigation
          control-color="primary"
          infinite
          :autoplay="true"
          transition-prev="slide-right"
          transition-next="slide-left"
          keep-alive
      >
        <q-carousel-slide name="1hea" class="row items-center">
          <img src="../../assets/images/plots/heatmap_general.png" class="example-image"
               alt="Heatmap plot for a single class"/>
        </q-carousel-slide>
        <q-carousel-slide name="2hea" class="row items-center">
          <img src="../../assets/images/plots/heatmap_multi.png" class="example-image"
               alt="Heatmap plot for multiple classes"/>
        </q-carousel-slide>
      </q-carousel>

      <q-carousel
          v-if="plotType === 'EMBEDDING'"
          animated
          v-model="slideEmb"
          navigation
          control-color="primary"
          infinite
          :autoplay="true"
          transition-prev="slide-right"
          transition-next="slide-left"
          keep-alive
      >
        <q-carousel-slide name="1emb" class="row items-center">
          <img src="../../assets/images/plots/embedding_general.png" class="example-image"
               alt="Embedding plot for a single class"/>
        </q-carousel-slide>
        <q-carousel-slide name="2emb" class="row items-center">
          <img src="../../assets/images/plots/embedding_multi.png" class="example-image"
               alt="Embedding plot for multiple classes"/>
        </q-carousel-slide>
      </q-carousel>

    </div>
  </div>

</template>

<script>
export default {
  name: "PlotInfo",
  data() {
    return {
      slideDec: "1dec",
      slideIma: "1ima",
      slideSca: "1sca",
      slideSum: "1sum",
      slideWat: "1wat",
      slideHea: "1hea",
      slideEmb: "1emb",
    }
  },
  props: {
    plotType: null
  },
}

</script>

<style scoped>
.example-image {
  width: 100%;
  height: 100%;
  object-fit: contain;
  background: white;
  border-radius: 5px;
  padding-top: 16px;
  padding-bottom: 32px;
}
</style>