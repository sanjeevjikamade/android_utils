# android_utils
## Signature Validater: 
Validates app for the signature by with it is signed. If wrong signature found it esits the app.

### Usage:
if(!SignatureValidator.isValidSignature(MainActivity.this)) {
	MainActivity.this.finish();
	return;
  }
