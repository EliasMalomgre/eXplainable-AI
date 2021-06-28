<template>

  <div id="about-section" class="q-pa-lg">

    <div class="q-pa-lg container">
      <div><h3 class="q-ma-none text-primary">About</h3></div>
      <div class="text-caption">To help you on the way</div>
    </div>


    <div id="read-section">
      <div class="row content-center justify-center container">
        <q-card id="what-is-xai" class="q-mb-lg q-pa-sm card-about" flat bordered>
          <h5 class="text-primary">What is XAI?</h5>
          <q-card-section class="col-8 text-body2">

            <p>
              XAI is an abbreviation for eXplainable Artificial Intelligence, a new and rising technology trend, which
              attempts to open
              the black box of typical AI models and explain their inner workings.
            </p>
            <p>
              Besides a set of visualisation methods and goals, it also offers a set of principles. To help developers
              make their Artificial Intelligence more transparant and trustworthy!
            </p>

          </q-card-section>
        </q-card>

        <q-card id="where-to-start" class="q-mb-lg q-pa-sm  card-about" flat bordered>
          <h5 class="text-primary">Where do I start?</h5>
          <q-card-section class="col-8 text-body2">

            <p>
              To get started with Xaii, we first need you to upload a model of your choosing. We support several
              technologies:
            </p>

            <q-card flat class="row justify-center items-center bg-white q-mb-sm">

              <img src="../assets/images/logos/keras.png" alt="keras' logo" class="col-sm-6 col-lg-3 q-pa-lg"
                   style="height: 7em; width:auto;"/>

              <img src="../assets/images/logos/tensorflow.png" alt="tensorflow's logo"
                   class="col-sm-6 col-lg-3 q-pa-lg" style="height: 7em; width:auto;"/>

              <img src="../assets/images/logos/onnx.png" alt="onnx's logo" class="col-sm-6 col-lg-3 q-pa-lg"
                   style="height: 7em; width:auto;"/>
            </q-card>


            <p>
              Head over to the Upload page!
            </p>

          </q-card-section>
          <q-card-actions class="row justify-end">
            <q-btn color="primary"
                   class="col-5"
                   @click="routeToUpload"
                   label="go upload an AI"
                   icon-right="send"
                   unelevated
                   rounded
            />
          </q-card-actions>
        </q-card>

        <q-card id="what-is-shap" class="q-mb-lg q-pa-sm card-about" flat bordered>
          <h5 class="text-primary">What is SHAP?</h5>
          <q-card-section class="col-8 text-body2">
            <p>
              SHAP (SHapley Additive exPlanations) is a game theoretic approach to explain the output of any machine
              learning model. It connects optimal credit allocation with local explanations using the classic Shapley
              values from game theory and their related extensions.
            </p>
            <p>
              SHAP assigns each feature an importance value for a particular prediction. The collective SHAP values can
              show how much each predictor contributes, either positively or negatively, to the target variable. Each
              observation gets its own set of SHAP values. This greatly increases its transparency. We can explain why a
              case receives its prediction and the contributions of the predictors.
            </p>
            <p>
              It is important to point out the SHAP values do not provide causality.
            </p>
            <p align="right">
              Visit the official SHAP github: <a href="https://github.com/slundberg/shap" target="_blank"
                                                 class="text-primary">github</a>
            </p>
            <p align="right">
              Read their documentation: <a href="https://shap.readthedocs.io/en/latest/index.html" target="_blank"
                                           class="text-primary">shap.readthedocs.io</a>
            </p>
          </q-card-section>
        </q-card>

        <q-card id="what-are-explainers" class="q-mb-lg q-pa-sm card-about" flat bordered>
          <h5 class="text-primary">What are SHAP explainers?</h5>

          <q-card-section>

            <q-option-group
                v-model="taskType"
                :options="taskTypeOptions"
                color="primary"
                label="Pick an explainer"
                align="center"
                inline
                class="q-mb-md"
            />

            <explainer-info :task-type="taskType"/>
          </q-card-section>
        </q-card>

        <q-card id="what-plots-can-i-use" class="q-mb-lg q-pa-sm card-about" flat bordered>
          <h5 class="text-primary">Which plots can I use?</h5>
          <q-card-section>
            <q-option-group
                v-model="plotType"
                :options="plotOptions"
                color="primary"
                label="Pick a plot"
                align="center"
                inline
                class="q-mb-md"
            />
            <plot-info :plot-type="plotType"/>
          </q-card-section>
        </q-card>

        <q-card id="who-are-we" class="q-mb-lg q-pa-sm card-about" flat bordered>
          <h5 class="text-primary">About us!</h5>
          <q-card-section class="col-8 text-body2">
            <p>
              We're two graduating computer science students at the
              <a href="https://www.kdg.be/en" class="text-primary">Antwerp Karel De Grote University College</a>,
              with a passion for artificial intelligence, complex questions and simple answers.
            </p>

            <div class="row items-start justify-around">
              <div class="col-lg-2"/>
              <div class="col-xs-4 col-sm-3" align="center">
                <div class="container-about-us">
                  <img class="img-about-us" src="../assets/images/us/FaceElias.png"/>
                  <q-btn type="a"
                         href="https://www.linkedin.com/in/elias-malomgr%C3%A9-714abb208/"
                         target="_blank"
                         class="li-btn"
                         color="primary"
                         unelevated
                         round
                         size="md"
                         icon="fab fa-linkedin-in"/>
                </div>
                <h6 class="text-primary">Elias Malomgré</h6>
              </div>
              <div class="col-xs-4 col-sm-3" align="center">
                <div class="container-about-us">
                  <img class="img-about-us" src="../assets/images/us/FaceVink.png"/>
                  <q-btn type="a"
                         href="https://www.linkedin.com/in/vinkvdb/"
                         target="_blank"
                         class="li-btn"
                         color="primary"
                         unelevated
                         round
                         size="md"
                         icon="fab fa-linkedin-in"/>
                </div>
                <h6 class="text-primary">Vink Van den Bosch</h6>
              </div>
              <div class="col-lg-2"/>
            </div>

            <p>
              This application was developed in collaboration with
              <a href="https://www.infosupport.com/" target="_blank" class="text-primary">Info Support</a> during our
              internship. They
              were a wonderful partner and we urge you to check them.
            </p>
          </q-card-section>
        </q-card>

        <q-card id="what-is-onnx" class="q-mb-lg q-pa-sm card-about" flat bordered>
          <h5 class="text-primary">What is ONNX?</h5>
          <q-card-section class="col-8 text-body2">
            <p>
              ONNX is a toolkit that allows you to easily transform between neural net technologies. Seeing as not every
              technology is covered straight out of the box by Xaii, we advise using ONNX to help you transform your
              neural network to a supported technology!
            </p>

            <p>
              Xaii is mainly build to work with Tensorflow Keras models, and ONNX might not be able to flawlessly
              convert every feature from another technology. So be warned that conversion can result in unexpected
              incompatibilities popping up.
            </p>
            <p align="right">
              Visit their page: <a href="https://onnx.ai/" target="_blank"
                                   class="text-primary">onnx.ai</a>
            </p>

          </q-card-section>

        </q-card>

        <q-card id="see-others" class="q-mb-lg q-pa-sm card-about" flat bordered>
          <h5 class="text-primary">Are there other interesting tools?</h5>
          <q-card-section class="col-8 text-body2">
            <p>
              During our research we came across several other amazing projects that we'd love to mention here. Their
              tools can help you further understand the intricacies of Artificial Intelligence, by offering you
              different
              perspectives on the matter.
            </p>

            <h6>CNN Explainer</h6>
            <p>
              'CNN Explainer' can help you understand how a typical image recognition neural net propagates information
              through the different layers, and how different layers function. It even allows you to upload your own
              image and see how the neural net interprets it. It's the amazing result of a research
              collaboration between Georgia Tech and Oregon State.
            </p>
            <p align="right">
              Go take a look: <a href="https://poloclub.github.io/cnn-explainer/" target="_blank"
                                 class="text-primary">poloclub.github</a>
            </p>

            <h6>NN-SVG</h6>
            <p>
              'NN-SVG' by Alexander LeNail is an amazing visualisation tool, which can help you better understand how
              different layers in a typical neural net are interconnected. It offers three different visualisation
              styles, namely FCNN, LeNet and AlexNet. With several customisation options.
            </p>
            <p align="right">
              Try it out: <a href="http://alexlenail.me/NN-SVG/index.html" target="_blank"
                             class="text-primary">NN-SVG</a>
            </p>

            <h6>Tensorflow Playground</h6>
            <p>
              The amazing people at Tensorflow developed 'Tensor Playground' to help you understand the influence of
              different parameters. it simulates the training process and the interactions between different layers. The
              complexity of the layers is limited, but the real-time aspect is asbolutely wonderful.
            </p>
            <p align="right">
              Give it a go: <a href="https://playground.tensorflow.org/" target="_blank"
                               class="text-primary">playground.tensorflow</a>
            </p>
          </q-card-section>

        </q-card>

      </div>


    </div>


  </div>
</template>

<script>

import PlotInfo from "@/components/misc/PlotIInfo";
import ExplainerInfo from "@/components/misc/ExplainerInfo";

export default {
  name: 'About',
  data() {
    return {
      // bundleId: null,
      taskType: "GRADIENT",
      plotType: "DECISION",
      taskTypeOptions: [
        {
          label: 'Gradient Explainer (SHAP)',
          value: 'GRADIENT',
        },
        {
          label: 'Deep Explainer (SHAP)',
          value: 'DEEP',
        },
        {
          label: 'Kernel Explainer (SHAP)',
          value: 'KERNEL',
        },
      ],
      plotOptions: [
        {
          label: 'Decision Plot (SHAP)',
          value: 'DECISION',
        },
        {
          label: 'Image Plot (SHAP)',
          value: 'IMAGE',
        },
        {
          label: 'Embedding Plot (SHAP)',
          value: 'EMBEDDING',
        },
        {
          label: 'Heatmap Plot (SHAP)',
          value: 'HEATMAP',
        },
        {
          label: 'Scatter Plot (SHAP)',
          value: 'SCATTER',
        },
        {
          label: 'Summary Plot (SHAP)',
          value: 'SUMMARY',
        },
        {
          label: 'Waterfall Plot (SHAP)',
          value: 'WATERFALL',
        },
      ],
    }
  },
  components: {ExplainerInfo, PlotInfo},
  methods: {
    routeToUpload() {
      this.$router.push({name: 'Upload'})
    },
  },
}
</script>

<style scoped>
.card-about {
  width: 100%;
}

.img-about-us {
  border-radius: 200px;
  width: 100%;
  max-width: 200px;
}

.container-about-us {
  position: relative;
  width: 100%;
  max-width: 400px;
}

.li-btn {
  position: absolute;
  top: 80%;
  left: 80%;
  transform: translate(-50%, -50%);
  -ms-transform: translate(-50%, -50%);
}

</style>

