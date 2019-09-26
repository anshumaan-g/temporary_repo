class random:
  def detect_test_extensions(self):
    # Python C API test module
    self.add(Extension('_testcapi', ['_testcapimodule.c'],
                       depends=['testcapi_long.h']))

    # Python Internal C API test module
    self.add(Extension('_testinternalcapi', ['_testinternalcapi.c'],
                       extra_compile_args=['-DPy_BUILD_CORE_MODULE']))

    # Python PEP-3118 (buffer protocol) test module
    self.add(Extension('_testbuffer', ['_testbuffer.c']))

    # Test loading multiple modules from one compiled file (http://bugs.python.org/issue16421)
    self.add(Extension('_testimportmultiple', ['_testimportmultiple.c']))

    # Test multi-phase extension module init (PEP 489)
    self.add(Extension('_testmultiphase', ['_testmultiphase.c']))

    # Fuzz tests.
    self.add(Extension('_xxtestfuzz',
                       ['_xxtestfuzz/_xxtestfuzz.c',
                        '_xxtestfuzz/fuzzer.c']))
