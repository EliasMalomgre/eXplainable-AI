<template>
  <div v-show="taskType !== null">
    <div v-show="taskType === 'NORMAL'">
      <p>
        Uses Shapley values to explain any machine learning model or python function.
        This is the primary explainer interface for the SHAP library.
      </p>
    </div>
    <div v-show="taskType === 'GRADIENT'">
      <p>
        An implementation of expected gradients to approximate SHAP values for deep learning models. It is based on connections between SHAP and the Integrated Gradients algorithm. Gradient Explainer is slower than Deep Explainer and makes different approximation assumptions, but is more stable than Deep Explainer when running on small GPU's. We recommend Gradient Explainer, since Deep Explainer requires a lot of available VRAM and might fail, causing the algorithm to use the CPU instead.
      </p>
      <p>
        Expected gradients is an extension of the integrated gradients method (Sundararajan et al. 2017), a feature attribution method designed for differentiable models based on an extension of Shapley values to infinite player games (Aumann-Shapley values). Integrated gradients values are a bit different from SHAP values, and require a single reference value to integrate from. As an adaptation to make them approximate SHAP values, expected gradients reformulates the integral as an expectation and combines that expectation with sampling reference values from the background dataset. This leads to a single combined expectation of gradients that converges to attributions that sum to the difference between the expected model output and the current output.
      </p>
    </div>
    <div v-show="taskType === 'DEEP'">
      <p>
        An implementation of Deep SHAP, a faster (but only approximate) algorithm to compute SHAP values for deep learning models that are based on connections between SHAP and the DeepLIFT algorithm.
      </p>
      <p>
        <span class="text-warning">WARNING: </span>Deep Explainer requires a lot of available VRAM and might fail, causing the algorithm to use the CPU instead.
      </p>
      <p>
        Deep SHAP is a high-speed approximation algorithm for SHAP values in deep learning models and is an enhanced version of the DeepLIFT algorithm where, similar to Kernel SHAP, we approximate the conditional expectations of SHAP values using a selection of background samples. Lundberg and Lee, NIPS 2017 showed that the per node attribution rules in DeepLIFT (Shrikumar, Greenside, and Kundaje, arXiv 2017) can be chosen to approximate Shapley values. The implementation here differs from the original DeepLIFT by using a distribution of background samples instead of a single reference value and using Shapley equations to linearize components such as max, softmax, products, divisions, etc. Note that some of these enhancements have also been since integrated into DeepLIFT. By integrating over many background samples Deep estimates approximate SHAP values such that they sum up to the difference between the expected model output on the passed background samples and the current model output (f(x) - E[f(x)]).
      </p>
    </div>
    <div v-show="taskType === 'KERNEL'">
      <p>
        An implementation of Kernel SHAP, a model agnostic method to estimate SHAP values for any model. Because it makes no assumptions about the model type, KernelExplainer is slower than the other model type specific algorithms.
      </p>
      <p>
        <span class="text-warning">WARNING: </span>Kernel Explainer is significantly slower than Deep Explainer.
      </p>
      <p>
        Kernel SHAP is a method that uses a special weighted linear regression to compute the importance of each feature. The computed importance values are Shapley values from game theory and also coefficients from a local linear regression.
      </p>
    </div>
  </div>
</template>

<script>
export default {
  name: "ExplainerInfo",
  props: {
    taskType: null
  },
}

</script>

<style scoped>

</style>