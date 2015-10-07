# haystack-validator
Rules that can be run against a Project Haystack database to validate the tags

# License
This code is licensed under MPL 2.0, the details for which you can find here: https://www.mozilla.org/en-US/MPL/2.0/. There are two main goals for using this license:

1. You may use this software for free in your commercial or non-commercial products.
2. Any changes/derivations that you make to this software must be provided as open source, ideally as a contribution back to the original repository (as, say, a pull request). This includes ports to other languages.

# Overview
This code is not a finished product. It is a proof of concept with a comprehensive rule set gleaned from the Project-Haystack tag definition texts.

To get started quickly, this repo should be cloned into an Eclipse project, from which it is very easy to execute. See:

    src/test/java/ai/serotonin/haystack/validator/Test

... for simple examples of how to run against a local diffs file or a remote Haystack database.
