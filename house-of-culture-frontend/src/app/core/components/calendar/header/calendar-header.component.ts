import {Component, Input, Output, EventEmitter} from '@angular/core';
import {CalendarModule, CalendarView} from 'angular-calendar';
import {CommonModule} from "@angular/common";
import {MatButtonToggleModule} from "@angular/material/button-toggle";
import {MatButtonModule} from "@angular/material/button";
import {MatIconModule} from "@angular/material/icon";

@Component({
  selector: 'app-calendar-header',
  standalone: true,
  imports: [CommonModule, CalendarModule, MatButtonToggleModule, MatButtonModule, MatIconModule],
  templateUrl: './calendar-header.component.html',
  styleUrls: ['./calendar-header.component.scss']
})
export class CalendarHeaderComponent {
  @Input() view!: CalendarView;
  @Input() viewDate!: Date;
  @Input() hideViewButtons = false;
  @Input() hideArrows = false;
  @Output() viewChange = new EventEmitter<CalendarView>();
  @Output() viewDateChange = new EventEmitter<Date>();

  CalendarView = CalendarView;
}
