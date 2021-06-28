# Explainers implemented in Xaii

## Kernel Explainer (SHAP)

An implementation of Kernel SHAP, a model agnostic method to estimate SHAP values for any model. Because it makes no assumptions about the model type, KernelExplainer is slower than the other model type specific algorithms.

Kernel SHAP is a method that uses a special weighted linear regression to compute the importance of each feature. The computed importance values are Shapley values from game theory and also coefficients from a local linear regression.

## Gradient Explainer (SHAP)

An implementation of expected gradients to approximate SHAP values for deep learning models. It is based on connections between SHAP and the Integrated Gradients algorithm. Gradient Explainer is slower than Deep Explainer and makes different approximation assumptions, but is more stable than Deep Explainer when running on small GPU's. We recommend Gradient Explainer, since Deep Explainer requires a lot of available VRAM and might fail, causing the algorithm to use the CPU instead.

Expected gradients is an extension of the integrated gradients method (Sundararajan et al. 2017), a feature attribution method designed for differentiable models based on an extension of Shapley values to infinite player games (Aumann-Shapley values). Integrated gradients values are a bit different from SHAP values, and require a single reference value to integrate from. As an adaptation to make them approximate SHAP values, expected gradients reformulates the integral as an expectation and combines that expectation with sampling reference values from the background dataset. This leads to a single combined expectation of gradients that converges to attributions that sum to the difference between the expected model output and the current output.

## Deep Explainer (SHAP)

An implementation of Deep SHAP, a faster (but only approximate) algorithm to compute SHAP values for deep learning models that are based on connections between SHAP and the DeepLIFT algorithm.

WARNING: Deep Explainer requires a lot of available VRAM and might fail, causing the algorithm to use the CPU instead.

Deep SHAP is a high-speed approximation algorithm for SHAP values in deep learning models and is an enhanced version of the DeepLIFT algorithm where, similar to Kernel SHAP, we approximate the conditional expectations of SHAP values using a selection of background samples. Lundberg and Lee, NIPS 2017 showed that the per node attribution rules in DeepLIFT (Shrikumar, Greenside, and Kundaje, arXiv 2017) can be chosen to approximate Shapley values. The implementation here differs from the original DeepLIFT by using a distribution of background samples instead of a single reference value and using Shapley equations to linearize components such as max, softmax, products, divisions, etc. Note that some of these enhancements have also been since integrated into DeepLIFT. By integrating over many background samples Deep estimates approximate SHAP values such that they sum up to the difference between the expected model output on the passed background samples and the current model output (f(x) - E[f(x)]).

# Read more
**SHAP's Github**
https://github.com/slundberg/shap

**SHAP Docs**
https://shap.readthedocs.io/en/latest/index.html
https://shap-lrjball.readthedocs.io/en/latest/api.html#core-explainers

**Code Examples**
https://shap-lrjball.readthedocs.io/en/latest/examples.html