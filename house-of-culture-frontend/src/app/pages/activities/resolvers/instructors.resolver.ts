import {ResolveFn} from '@angular/router';
import {inject} from "@angular/core";
import {UserInfoTs} from "../../../../../out/api";
import {InstructorsService} from "../services/instructors.service";

export const instructorsResolver: ResolveFn<UserInfoTs[]> = () => {
  return inject(InstructorsService).getInstructors()
};
