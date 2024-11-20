import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Event} from "../calendar-form.component";
import {MatDialog} from "@angular/material/dialog";
import {
  EditedEvent,
  EditSingleEventPopupComponent
} from "../../edit-single-event-popup/edit-single-event-popup.component";

@Component({
  selector: 'app-added-event-cards',
  templateUrl: './added-event-cards.component.html',
  styleUrls: ['./added-event-cards.component.scss']
})
export class AddedEventCardsComponent implements OnInit{
  @Input() addedEvents?: Event[];
  @Input() isThisEdit?: boolean;

  @Output() eventRemoved = new EventEmitter<number>();
  @Output() eventChanged = new EventEmitter<{ updatedEventInfo: EditedEvent, event: Event }>();
  isScrollable: boolean = false;

  constructor(private dialog: MatDialog) {
  }

  ngOnInit(): void {
  }

  getMaxHeight() {
    if (this.addedEvents) {
      this.isScrollable = this.addedEvents?.length > 2;
      return this.isScrollable ? '230px' : 'auto';
    }
    return '230px'
  }

  removeEvent(index: number) {
    this.eventRemoved.emit(index);
  }

  openEditEventDialog(eventInfo: Event): void {
    const dialogRef = this.dialog.open(EditSingleEventPopupComponent, {
      width: '400px',
      data: {event: eventInfo, isEdit: this.isThisEdit}
    });

    dialogRef.afterClosed().subscribe((updatedEventInfo: EditedEvent) => {
      if (updatedEventInfo) {
        this.eventChanged.emit({updatedEventInfo: updatedEventInfo, event: eventInfo})
      }
    });
  }
}
