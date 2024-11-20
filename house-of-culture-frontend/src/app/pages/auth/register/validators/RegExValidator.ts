import {AbstractControl, ValidationErrors, ValidatorFn} from "@angular/forms";

export class RegExValidators {
  constructor() {
  }

  static patternValidator(regex: RegExp, error: ValidationErrors) {
    return (control: AbstractControl) => {
      if (!control.value) {
        return null;
      }

      const valid = regex.test(control.value);

      return valid ? null : error;
    };
  }

  // @ts-ignore
  static MatchPasswordValidator(control: AbstractControl): ValidatorFn | ValidatorFn[] | null {
    // @ts-ignore
    const password: string = control.get("password").value;
    // @ts-ignore
    const confirmPassword: string = control.get("confirmPassword").value;

    if (!confirmPassword?.length) {
      return null;
    }

    if (password !== confirmPassword) {
      // @ts-ignore
      control.get("confirmPassword").setErrors({mismatch: true});
    } else {
      return null;
    }

  }
}
